package com.example.logger.Service;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Service {

    @KafkaListener(topics = "logs", groupId = "group1")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }

}

