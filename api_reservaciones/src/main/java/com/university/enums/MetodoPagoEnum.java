package com.university.enums;

public enum MetodoPagoEnum {
    TRANSFERENCIA("Transferencia"),
    TARJETA("Tarjeta");

    private final String nombre;

    private MetodoPagoEnum(String nombre) {
            this.nombre = nombre;
    }

    @Override
    public String toString() {
            return this.nombre;
    }

    public String getNombre() {
            return this.nombre;
    }
}
