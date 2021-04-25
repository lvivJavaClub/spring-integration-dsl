package com.example.demo;

import com.example.demo.model.HelloEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnablePublisher;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.integration.mail.MailSendingMessageHandler;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.sql.DataSource;
import java.io.File;
import java.util.concurrent.Executors;

@EnableIntegration
@EnablePublisher
@Configuration
public class Flows {

    @Autowired
    JavaMailSender javaMailSender;

//    @Bean("foodSource")
//    public MessageSource<?> foodSource(DataSource dataSource) {
//        return new JdbcPollingChannelAdapter(dataSource, "SELECT * FROM food");
//    }

//    @Bean
//    public IntegrationFlow dbFlow(DataSource dataSource) {
//        return IntegrationFlows
//                .from(foodSource(dataSource), spc -> spc.poller(Pollers.fixedDelay(1000)))
//                .transform(Transformers.toJson())
//                .handle(System.out::println)
//                .get();
//    }

    @Bean
    public IntegrationFlow filesFlow() {
        return IntegrationFlows
                .from(Files.inboundAdapter(new File("./monitore")), spec -> spec.poller(Pollers.fixedDelay(1000)))
                .publishSubscribeChannel(Executors.newCachedThreadPool(), s -> s
                        .id("filesPublishSubcribe")
                        .subscribe(f -> f
                                .filter(File.class, file -> file.getName().endsWith(".txt"))
                                .handle(System.out::println)
                        )
                        .subscribe(f -> f
                                .enrichHeaders(h -> h
                                        .headerFunction("file_type", message ->
                                                StringUtils.substringAfterLast(message.getHeaders().get("file_name", String.class), ".")
                                        ))
                                .handle(System.out::println)
                        )
                )
                .get();
    }

    @Bean
    public IntegrationFlow fileToEmail() {
        return IntegrationFlows
            .from("filesPublishSubcribe")
            .transform(File.class, file -> {
                var simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setSubject("Test subject");
                simpleMailMessage.setTo("club@java.lviv");
                simpleMailMessage.setText(file.getName());
                return simpleMailMessage;
            })
            .handle(mailSendingMessageHandler())
            .get();
    }

    @Bean
    public MailSendingMessageHandler mailSendingMessageHandler() {
        return new MailSendingMessageHandler(javaMailSender);
    }

    @Bean
    public ApplicationEventListeningMessageProducer eventsProducer() {
        var applicationEventListeningMessageProducer = new ApplicationEventListeningMessageProducer();
        applicationEventListeningMessageProducer.setEventTypes(HelloEvent.class);
        return applicationEventListeningMessageProducer;
    }

    @Bean
    public IntegrationFlow applicationEventsFlow() {
        return IntegrationFlows.from(eventsProducer())
                .transform(Transformers.toJson())
                .handle(System.out::println)
                .get();
    }


    @Bean
    public IntegrationFlow publisherFlow() {
        return IntegrationFlows
                .from("greetings")
                .handle(System.out::println)
                .get();
    }

}
