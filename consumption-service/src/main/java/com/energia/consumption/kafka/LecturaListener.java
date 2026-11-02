package com.energia.consumption.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.energia.consumption.lectura.Lectura;
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
        repository.save(new Lectura(evento.medidorId(), evento.momento(), evento.kwh()));
        log.info("Guardada lectura de {}", evento.medidorId());
    }
}