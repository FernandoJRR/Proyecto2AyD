package com.university.enums;

public enum EstadoReservacionEnum {
    PROGRAMADA("Programada"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String nombre;

    private EstadoReservacionEnum(String nombre) {
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
