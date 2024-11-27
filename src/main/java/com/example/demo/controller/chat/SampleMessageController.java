package com.example.demo.controller.chat;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequiredArgsConstructor
public class SampleMessageController {

    private static final String EXCHANGE_NAME = "sample.exchange";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sample/queue")
    public String samplePublish() {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sample.routing.#", "RabbitMQ + SpringBoot = Success");
        return "Message Sending!!";
    }
}
