package com.energia.consumption.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.energia.consumption.lectura.LecturaRepository;

@Component
public class LecturaListener {

    private static final Logger log = LoggerFactory.getLogger(LecturaListener.class);

    private final LecturaRepository repository;

    public LecturaListener(LecturaRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "lecturas-medidor")
    public void recibir(LecturaMedidor evento) {
        validar(evento);

        // Si la lectura ya estaba guardada (Kafka la ha entregado dos veces),
        // la base de datos la descarta y nos devuelve 0
        int insertadas = repository.guardarSiNoExiste(evento.medidorId(), evento.momento(), evento.kwh());

        if (insertadas == 0) {
            log.warn("Lectura duplicada ignorada: {} {}", evento.medidorId(), evento.momento());
        } else {
            log.info("Guardada lectura de {}", evento.medidorId());
        }
    }

    private void validar(LecturaMedidor evento) {
        if (evento.medidorId() == null || evento.medidorId().isBlank()) {
            throw new LecturaInvalidaException("Lectura sin id de contador");
        }
        if (evento.momento() == null) {
            throw new LecturaInvalidaException("Lectura sin momento del contador " + evento.medidorId());
        }
        if (evento.kwh() < 0) {
            throw new LecturaInvalidaException("Consumo negativo del contador " + evento.medidorId() + ": " + evento.kwh());
        }
    }
}