package com.energia.consumption.coste;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Cliente HTTP de tariff-service. */
@Component
public class TarifaClient {

    private final RestClient restClient;

    public TarifaClient(@Value("${tarifas.url}") String urlTarifas) {
        // Nunca llames a otro servicio sin timeout: si tarda, no queremos
        // quedarnos esperando para siempre
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .baseUrl(urlTarifas)
                .requestFactory(requestFactory)
                .build();
    }

    public List<TarifaDto> tarifas() {
        return restClient.get()
                .uri("/api/tarifas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TarifaDto>>() {});
    }

    public List<PrecioHoraDto> precios(String tarifaId, LocalDate fecha) {
        return restClient.get()
                .uri("/api/tarifas/{id}/precios?fecha={fecha}", tarifaId, fecha)
                .retrieve()
                .body(new ParameterizedTypeReference<List<PrecioHoraDto>>() {});
    }
}