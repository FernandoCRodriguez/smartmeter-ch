package com.energia.tariff.tarifa;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

/** Las tarifas que ofrece la compañía. Precios de ejemplo en CHF/kWh, no reales. */
@Component
public class CatalogoTarifas {

    private final List<Tarifa> tarifas = List.of(
            new TarifaFija("fija", "Tarifa fija", new BigDecimal("0.28")),
            new TarifaPorFranjas("franjas", "Tarifa por franjas",
                    new BigDecimal("0.32"), new BigDecimal("0.22"),
                    LocalTime.of(7, 0), LocalTime.of(20, 0)),
            new TarifaDinamica("dinamica", "Tarifa dinámica", precios(
                    // 00-05 h: noche, poca demanda
                    "0.20", "0.19", "0.18", "0.18", "0.19", "0.21",
                    // 06-11 h: pico de la mañana
                    "0.26", "0.33", "0.35", "0.31", "0.25", "0.19",
                    // 12-17 h: mediodía con mucha producción solar
                    "0.16", "0.15", "0.17", "0.21", "0.27", "0.34",
                    // 18-23 h: pico de la tarde
                    "0.39", "0.38", "0.32", "0.27", "0.23", "0.21")));

    public List<Tarifa> todas() {
        return tarifas;
    }

    public Optional<Tarifa> buscar(String id) {
        return tarifas.stream()
                .filter(tarifa -> tarifa.id().equals(id))
                .findFirst();
    }

    private static List<BigDecimal> precios(String... valores) {
        return Arrays.stream(valores).map(BigDecimal::new).toList();
    }
}
