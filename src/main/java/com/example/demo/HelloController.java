package com.example.demo;

import com.example.demo.model.HelloEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HelloController {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final DemoService demoService;

    @GetMapping("/")
    public void sendHello(String name) {
        demoService.sayHello(name);
        applicationEventPublisher.publishEvent(new HelloEvent(name));
    }

}
