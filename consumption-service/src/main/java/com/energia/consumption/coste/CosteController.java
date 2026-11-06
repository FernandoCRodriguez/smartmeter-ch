package com.energia.consumption.coste;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api/consumo")
public class CosteController {

    private final CosteService costeService;

    public CosteController(CosteService costeService) {
        this.costeService = costeService;
    }

    /** GET /api/consumo/diario?fecha=2026-11-05: consumo y coste de cada contador (hoy si no se indica). */
    @GetMapping("/diario")
    public List<ConsumoDiario> diario(
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate dia = (fecha != null) ? fecha : LocalDate.now(CosteService.ZONA_SUIZA);
        return costeService.consumoDiario(dia);
    }

    /** Si tariff-service no responde, devolvemos un 503 claro en vez de un error 500 genérico. */
    @ExceptionHandler(RestClientException.class)
    public ProblemDetail tarifasNoDisponibles(RestClientException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "tariff-service no responde");
    }
}