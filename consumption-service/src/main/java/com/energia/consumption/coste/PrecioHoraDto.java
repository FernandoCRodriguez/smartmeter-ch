package com.energia.consumption.coste;

import java.math.BigDecimal;

/** El precio de una hora tal como lo devuelve tariff-service. */
public record PrecioHoraDto(int hora, BigDecimal precioKwh) {
}