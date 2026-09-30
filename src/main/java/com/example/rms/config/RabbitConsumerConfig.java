package com.example.rms.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitConsumerConfig {
    public static final String QUEUE_CUSTOMER = "rms.customer.sync.queue";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Hỗ trợ đọc/ghi LocalDateTime
        return mapper;
    }

    @Bean
    public Queue customerQueue() {
        return QueueBuilder.durable(QUEUE_CUSTOMER).build();
    }
}