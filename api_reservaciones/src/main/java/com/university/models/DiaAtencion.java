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

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true)
    private DiaSemanaEnum nombre;

    public DiaAtencion() {}

    public DiaAtencion(DiaSemanaEnum nombre) {
        this.nombre = nombre;
    }

    public DiaSemanaEnum getNombre() {
        return nombre;
    }

    public void setNombre(DiaSemanaEnum nombre) {
        this.nombre = nombre;
    }
}
