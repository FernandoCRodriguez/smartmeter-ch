package com.energia.consumption.kafka;

import java.time.Instant;

public record LecturaMedidor(String medidorId, Instant momento, double kwh) {
}