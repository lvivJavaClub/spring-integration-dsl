package com.example.demo;

import org.springframework.integration.annotation.Publisher;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {

    @Override
    @Publisher(channel = "greetings")
    public String sayHello(String name) {
        return "Hello to " + name;
    }
}
