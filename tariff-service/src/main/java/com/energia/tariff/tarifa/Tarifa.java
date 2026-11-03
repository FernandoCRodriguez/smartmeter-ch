package com.energia.tariff.tarifa;

/**
 * Una tarifa eléctrica. Es "sealed": solo pueden existir los tres tipos
 * que aparecen en permits, y el compilador lo sabe.
 */
public sealed interface Tarifa permits TarifaFija, TarifaPorFranjas, TarifaDinamica {

    String id();

    String nombre();
}