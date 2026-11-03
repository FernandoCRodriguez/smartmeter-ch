package com.energia.tariff.tarifa;

import java.math.BigDecimal;

/** El mismo precio a cualquier hora. */
public record TarifaFija(String id, String nombre, BigDecimal precioKwh) implements Tarifa {
}
