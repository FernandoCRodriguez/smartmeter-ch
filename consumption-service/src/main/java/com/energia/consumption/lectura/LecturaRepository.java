package com.energia.consumption.lectura;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LecturaRepository extends JpaRepository<Lectura, Long> {

    // Las 20 lecturas más recientes
    List<Lectura> findTop20ByOrderByMomentoDesc();

    // Consumo de cada contador en cada hora (hora de Suiza) entre dos instantes.
    // La suma la hace PostgreSQL: devuelve como mucho 24 filas por contador,
    // en vez de traer a Java miles de lecturas.
    @Query(value = """
            SELECT medidor_id AS "medidorId",
                   CAST(EXTRACT(HOUR FROM momento AT TIME ZONE 'Europe/Zurich') AS INTEGER) AS "hora",
                   SUM(kwh) AS "kwh"
            FROM lectura
            WHERE momento >= :desde AND momento < :hasta
            GROUP BY 1, 2
            ORDER BY 1, 2
            """, nativeQuery = true)
    List<ConsumoHora> consumoPorHora(@Param("desde") Instant desde, @Param("hasta") Instant hasta);
}