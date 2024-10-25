package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "estado_reservacion")
public class EstadoReservacion extends Auditor{

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    public EstadoReservacion() {}

    public EstadoReservacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}