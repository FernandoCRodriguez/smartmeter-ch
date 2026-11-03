package com.energia.tariff.tarifa;

import java.math.BigDecimal;
import java.util.List;

/** Un precio distinto para cada hora del día (24 precios, de las 00:00 a las 23:00). */
public record TarifaDinamica(String id, String nombre, List<BigDecimal> preciosPorHora) implements Tarifa {

    // Constructor compacto: valida los datos antes de crear el record
    public TarifaDinamica {
        if (preciosPorHora.size() != 24) {
            throw new IllegalArgumentException("Hacen falta 24 precios, uno por hora");
        }
        preciosPorHora = List.copyOf(preciosPorHora); // copia inmutable
    }
}
