package com.energia.consumption.coste;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
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

    // @Cacheable: la primera respuesta se guarda en memoria y las siguientes
    // llamadas ya no salen a la red.
    // @Retryable: si falla la conexión o salta el timeout, reintenta 2 veces
    // esperando 0,5 s y luego 1 s. Un error 404 no se reintenta.
    @Cacheable("tarifas")
    @Retryable(includes = ResourceAccessException.class, maxRetries = 2, delay = 500, multiplier = 2)
    public List<TarifaDto> tarifas() {
        return restClient.get()
                .uri("/api/tarifas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TarifaDto>>() {});
    }

    // Los precios de un día no cambian: se guardan por tarifa y fecha
    @Cacheable("precios")
    @Retryable(includes = ResourceAccessException.class, maxRetries = 2, delay = 500, multiplier = 2)
    public List<PrecioHoraDto> precios(String tarifaId, LocalDate fecha) {
        return restClient.get()
                .uri("/api/tarifas/{id}/precios?fecha={fecha}", tarifaId, fecha)
                .retrieve()
                .body(new ParameterizedTypeReference<List<PrecioHoraDto>>() {});
    }
}