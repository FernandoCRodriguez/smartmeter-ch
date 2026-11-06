package com.energia.consumption.coste;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.energia.consumption.lectura.ConsumoHora;
import com.energia.consumption.lectura.LecturaRepository;

@Service
public class CosteService {

    static final ZoneId ZONA_SUIZA = ZoneId.of("Europe/Zurich");

    private final LecturaRepository repository;
    private final TarifaClient tarifaClient;

    public CosteService(LecturaRepository repository, TarifaClient tarifaClient) {
        this.repository = repository;
        this.tarifaClient = tarifaClient;
    }

    /** Una tarifa con sus 24 precios del día, indexados por hora. */
    private record TarifaConPrecios(TarifaDto tarifa, Map<Integer, BigDecimal> precioPorHora) {
    }

    public List<ConsumoDiario> consumoDiario(LocalDate fecha) {
        // El día va de 00:00 a 00:00 en hora de Suiza, no del servidor
        Instant desde = fecha.atStartOfDay(ZONA_SUIZA).toInstant();
        Instant hasta = fecha.plusDays(1).atStartOfDay(ZONA_SUIZA).toInstant();
        List<ConsumoHora> consumos = repository.consumoPorHora(desde, hasta);

        // Una llamada a tariff-service por tarifa y día, no una por lectura
        List<TarifaConPrecios> tarifas = tarifaClient.tarifas().stream()
                .map(tarifa -> new TarifaConPrecios(tarifa, preciosPorHora(tarifa.id(), fecha)))
                .toList();

        // Agrupamos las filas por contador (TreeMap: ordenados por id)
        Map<String, List<ConsumoHora>> porMedidor = consumos.stream()
                .collect(Collectors.groupingBy(ConsumoHora::getMedidorId, TreeMap::new, Collectors.toList()));

        return porMedidor.entrySet().stream()
                .map(entrada -> calcular(entrada.getKey(), entrada.getValue(), tarifas))
                .toList();
    }

    private Map<Integer, BigDecimal> preciosPorHora(String tarifaId, LocalDate fecha) {
        return tarifaClient.precios(tarifaId, fecha).stream()
                .collect(Collectors.toMap(PrecioHoraDto::hora, PrecioHoraDto::precioKwh));
    }

    private ConsumoDiario calcular(String medidorId, List<ConsumoHora> horas, List<TarifaConPrecios> tarifas) {
        double kwhTotal = horas.stream().mapToDouble(ConsumoHora::getKwh).sum();
        List<CosteTarifa> costes = tarifas.stream()
                .map(t -> new CosteTarifa(t.tarifa().id(), t.tarifa().nombre(), coste(horas, t.precioPorHora())))
                .toList();
        return new ConsumoDiario(medidorId, kwhTotal, costes);
    }

    // Coste = suma, hora a hora, de (kWh consumidos en esa hora × precio de esa hora)
    private BigDecimal coste(List<ConsumoHora> horas, Map<Integer, BigDecimal> precioPorHora) {
        return horas.stream()
                .map(h -> BigDecimal.valueOf(h.getKwh()).multiply(precioPorHora.get(h.getHora())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(4, RoundingMode.HALF_UP);
    }
}