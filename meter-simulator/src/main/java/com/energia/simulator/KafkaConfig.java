package com.energia.simulator;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_LECTURAS = "lecturas-medidor";

    @Bean
    NewTopic topicLecturas() {
        return TopicBuilder.name(TOPIC_LECTURAS)
                .partitions(3) // hasta 3 consumidores en paralelo
                .replicas(1)   // solo tenemos un broker
                .build();
    }
}