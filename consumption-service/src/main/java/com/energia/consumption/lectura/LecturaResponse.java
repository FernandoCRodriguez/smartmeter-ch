package com.energia.consumption.lectura;

import java.time.Instant;

public record LecturaResponse(Long id, String medidorId, Instant momento, double kwh) {

    static LecturaResponse desde(Lectura lectura) {
        return new LecturaResponse(
                lectura.getId(), lectura.getMedidorId(), lectura.getMomento(), lectura.getKwh());
    }
}