package com.university.enums;

public enum DiaSemanaEnum {
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miercoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sabado"),
    DOMINGO("Domingo");

    private final String nombre;

    private DiaSemanaEnum(String nombre) {
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
