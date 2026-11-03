package com.energia.tariff.tarifa;

import java.math.BigDecimal;
import java.time.LocalTime;

/** Precio alto de lunes a viernes entre inicioAlto y finAlto; precio bajo el resto del tiempo. */
public record TarifaPorFranjas(
        String id,
        String nombre,
        BigDecimal precioAlto,
        BigDecimal precioBajo,
        LocalTime inicioAlto,
        LocalTime finAlto) implements Tarifa {
}
