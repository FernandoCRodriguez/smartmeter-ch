package com.energia.simulator;

import java.time.Instant;

/** Evento publicado en Kafka: una lectura de un contador. */
public record LecturaMedidor(String medidorId, Instant momento, double kwh) {
}