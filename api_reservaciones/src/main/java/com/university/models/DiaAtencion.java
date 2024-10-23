package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.university.enums.DiaSemanaEnum;

@Entity
@Table(name = "dia_atencion")
public class DiaAtencion extends Auditor{

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    public DiaAtencion() {}

    public DiaAtencion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
