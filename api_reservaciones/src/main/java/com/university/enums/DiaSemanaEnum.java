package com.university.enums;

import java.time.DayOfWeek;

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

    public static DiaSemanaEnum fromDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return LUNES;
            case TUESDAY:
                return MARTES;
            case WEDNESDAY:
                return MIERCOLES;
            case THURSDAY:
                return JUEVES;
            case FRIDAY:
                return VIERNES;
            case SATURDAY:
                return SABADO;
            case SUNDAY:
                return DOMINGO;
            default:
                throw new IllegalArgumentException("Día no válido");
        }
    }
}
