package com.energia.consumption.coste;

import java.math.BigDecimal;

/** Lo que costaría el consumo de un contador con una tarifa. */
public record CosteTarifa(String tarifaId, String tarifa, BigDecimal chf) {
}