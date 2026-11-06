package com.energia.consumption.lectura;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface LecturaRepository extends JpaRepository<Lectura, Long> {

    // Las 20 lecturas más recientes
    List<Lectura> findTop20ByOrderByMomentoDesc();

    // Guarda la lectura solo si no existe ya otra del mismo contador y momento.
    // Devuelve 1 si la ha guardado y 0 si era un duplicado.
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO lectura (medidor_id, momento, kwh)
            VALUES (:medidorId, :momento, :kwh)
            ON CONFLICT (medidor_id, momento) DO NOTHING
            """, nativeQuery = true)
    int guardarSiNoExiste(@Param("medidorId") String medidorId,
                          @Param("momento") Instant momento,
                          @Param("kwh") double kwh);

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