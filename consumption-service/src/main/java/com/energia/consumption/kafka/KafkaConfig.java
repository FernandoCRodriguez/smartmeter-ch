package com.energia.consumption.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic topicLecturasDlt() {
        // Mismas particiones que lecturas-medidor: cada mensaje fallido se
        // publica en la misma partición que tenía el original
        return TopicBuilder.name("lecturas-medidor-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Spring Boot detecta este bean y lo aplica a todos los @KafkaListener
    @Bean
    DefaultErrorHandler errorHandler(KafkaTemplate<?, ?> kafkaTemplate) {
        // Cuando se agotan los reintentos, el mensaje se publica en
        // lecturas-medidor-dlt en vez de bloquear la partición
        var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        // 3 reintentos con 1 segundo de espera entre ellos
        var errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));

        // Una lectura inválida seguirá siéndolo: sin reintentos, directa al DLT
        errorHandler.addNotRetryableExceptions(LecturaInvalidaException.class);
        return errorHandler;
    }
}