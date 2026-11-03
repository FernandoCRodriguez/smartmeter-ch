package com.energia.tariff.tarifa;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private static final ZoneId ZONA_SUIZA = ZoneId.of("Europe/Zurich");

    private final CatalogoTarifas catalogo;
    private final CalculadoraPrecio calculadora;

    public TarifaController(CatalogoTarifas catalogo, CalculadoraPrecio calculadora) {
        this.catalogo = catalogo;
        this.calculadora = calculadora;
    }

    /** GET /api/tarifas: todas las tarifas. */
    @GetMapping
    public List<TarifaResponse> todas() {
        return catalogo.todas().stream()
                .map(TarifaResponse::desde)
                .toList();
    }

    /** GET /api/tarifas/{id}/precios?fecha=2026-11-03: los 24 precios de un día (hoy si no se indica). */
    @GetMapping("/{id}/precios")
    public List<PrecioHora> preciosDelDia(
            @PathVariable("id") String id,
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Tarifa tarifa = catalogo.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la tarifa " + id));
        LocalDate dia = (fecha != null) ? fecha : LocalDate.now(ZONA_SUIZA);

        return IntStream.range(0, 24)
                .mapToObj(hora -> new PrecioHora(hora, calculadora.precioKwh(tarifa, dia.atTime(hora, 0))))
                .toList();
    }
}