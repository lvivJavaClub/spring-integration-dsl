package com.example.demo.model;

import org.springframework.context.ApplicationEvent;

public class HelloEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public HelloEvent(Object source) {
        super(source);
    }
}
