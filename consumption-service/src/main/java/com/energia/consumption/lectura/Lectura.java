package com.energia.consumption.lectura;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Lectura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL genera el id
    private Long id;

    private String medidorId;
    private Instant momento;
    private double kwh;

    protected Lectura() {
        // JPA necesita un constructor vacío
    }

    public Lectura(String medidorId, Instant momento, double kwh) {
        this.medidorId = medidorId;
        this.momento = momento;
        this.kwh = kwh;
    }

    public Long getId() {
        return id;
    }

    public String getMedidorId() {
        return medidorId;
    }

    public Instant getMomento() {
        return momento;
    }

    public double getKwh() {
        return kwh;
    }
}