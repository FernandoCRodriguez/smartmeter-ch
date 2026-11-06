package com.energia.consumption.lectura;

/** Una fila del resultado de la consulta: lo que consumió un contador en una hora. */
public interface ConsumoHora {

    String getMedidorId();

    int getHora();

    double getKwh();
}