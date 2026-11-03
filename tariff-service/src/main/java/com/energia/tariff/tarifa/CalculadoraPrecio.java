package com.energia.tariff.tarifa;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Component;

@Component
public class CalculadoraPrecio {

    /** Precio del kWh en CHF para una tarifa en un momento (hora local de Suiza). */
    public BigDecimal precioKwh(Tarifa tarifa, LocalDateTime momento) {
        // Switch con patrones de Java 21. No hace falta "default": como Tarifa es
        // sealed, el compilador comprueba que cubrimos todos los tipos posibles.
        return switch (tarifa) {
            case TarifaFija(var id, var nombre, var precio) -> precio;
            case TarifaPorFranjas f when enHorarioAlto(f, momento) -> f.precioAlto();
            case TarifaPorFranjas f -> f.precioBajo();
            case TarifaDinamica d -> d.preciosPorHora().get(momento.getHour());
        };
    }

    private boolean enHorarioAlto(TarifaPorFranjas franjas, LocalDateTime momento) {
        DayOfWeek dia = momento.getDayOfWeek();
        boolean laborable = dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY;
        LocalTime hora = momento.toLocalTime();
        return laborable && !hora.isBefore(franjas.inicioAlto()) && hora.isBefore(franjas.finAlto());
    }
}
