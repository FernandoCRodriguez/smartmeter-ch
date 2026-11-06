package com.energia.consumption.coste;

import java.util.List;

/** El consumo de un contador en un día y su coste con cada tarifa. */
public record ConsumoDiario(String medidorId, double kwh, List<CosteTarifa> costes) {
}