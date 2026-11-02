package com.energia.simulator;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Simulador {

    private static final Logger log = LoggerFactory.getLogger(Simulador.class);

    private static final List<String> MEDIDORES = List.of(
            "CH-ZH-0001", "CH-ZH-0002", "CH-BE-0003", "CH-GE-0004", "CH-TI-0005");

    private final KafkaTemplate<String, LecturaMedidor> kafkaTemplate;

    // Inyección por constructor: Spring nos pasa el KafkaTemplate ya configurado
    public Simulador(KafkaTemplate<String, LecturaMedidor> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 5000) // cada 5 segundos
    public void enviarLecturas() {
        for (String medidorId : MEDIDORES) {
            double kwh = ThreadLocalRandom.current().nextDouble(0.05, 1.5);
            var lectura = new LecturaMedidor(medidorId, Instant.now(), kwh);

            // La clave es el id del contador: todas sus lecturas van a la
            // misma partición, y Kafka garantiza el orden dentro de ella
            kafkaTemplate.send(KafkaConfig.TOPIC_LECTURAS, medidorId, lectura);
        }
        log.info("Enviadas {} lecturas", MEDIDORES.size());
    }
}