package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "metodo_pago")
public class MetodoPago extends Auditor{

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    public MetodoPago() {}

    public MetodoPago(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}