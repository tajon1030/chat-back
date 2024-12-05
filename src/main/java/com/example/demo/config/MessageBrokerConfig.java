package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class MessageBrokerConfig {

    @Value("${spring.rabbitmq.exchange}")
    private String EXCHANGE_NAME;
    @Value("${spring.rabbitmq.queue}")
    private String QUEUE_NAME;
    @Value("${spring.rabbitmq.routing}")
    private String ROUTING_KEY;


    //RabbitAdmin을 사용하면 RabbitMQ 서버에 Exchange, Queue, Binding을 등록할 수 있음
    @Bean
    public AmqpAdmin amqpAdmin() {
        // RabbitTemplate을 사용하여 RabbitMQ 서버에 접근합니다.
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        rabbitAdmin.declareExchange(exchange());
        rabbitAdmin.declareQueue(ChatQueue());
        rabbitAdmin.declareBinding(bindingChat(exchange(), ChatQueue()));
        return rabbitAdmin;
    }

    // RabbitMQ와의 연결을 관리
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("username");
        factory.setPassword("password");
        return factory;
    }

    //Queue 등록
    @Bean
    public Queue ChatQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    //Binding 등록
    @Bean
    public Binding bindingChat(TopicExchange exchange, Queue queue) {
        //  exchange와 routing key의 패턴이 일치하는 queue에 메시지를 전달하겠다
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    //Exchange 등록
    @Bean
    TopicExchange exchange() {
        // Exchange 타입 중 Topic Exchange(패턴형식) 사용
        return new TopicExchange(EXCHANGE_NAME);

    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        //  메세지에 담을 Object를 rabbitmq의 메시지 형식(JSON body)으로 변환
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
