package com.energia.tariff.tarifa;

/** Lo que devuelve la API al listar las tarifas. */
public record TarifaResponse(String id, String nombre, String descripcion) {

    static TarifaResponse desde(Tarifa tarifa) {
        String descripcion = switch (tarifa) {
            case TarifaFija f -> "Mismo precio a cualquier hora";
            case TarifaPorFranjas f -> "Más cara de lunes a viernes de "
                    + f.inicioAlto() + " a " + f.finAlto();
            case TarifaDinamica d -> "El precio cambia cada hora";
        };
        return new TarifaResponse(tarifa.id(), tarifa.nombre(), descripcion);
    }
}
