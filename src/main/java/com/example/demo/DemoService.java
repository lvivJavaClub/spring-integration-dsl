package com.example.demo;

import org.springframework.integration.annotation.Publisher;

public interface DemoService {

    @Publisher(channel = "greetings")
    String sayHello(String name);

}
