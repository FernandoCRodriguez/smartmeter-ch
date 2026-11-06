package com.energia.consumption.kafka;

/** Una lectura con datos imposibles. Reintentar no la arreglaría: va directa al dead letter topic. */
public class LecturaInvalidaException extends RuntimeException {

    public LecturaInvalidaException(String mensaje) {
        super(mensaje);
    }
}