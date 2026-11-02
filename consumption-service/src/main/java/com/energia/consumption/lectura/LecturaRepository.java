package com.energia.consumption.lectura;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturaRepository extends JpaRepository<Lectura, Long> {

    // Las 20 lecturas más recientes
    List<Lectura> findTop20ByOrderByMomentoDesc();
}