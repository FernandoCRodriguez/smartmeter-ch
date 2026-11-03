package com.energia.tariff.tarifa;

import java.math.BigDecimal;

/** El precio del kWh en una hora del día (0 = de 00:00 a 01:00). */
public record PrecioHora(int hora, BigDecimal precioKwh) {
}