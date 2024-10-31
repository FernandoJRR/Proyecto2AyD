package com.university.models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "servicio_rol")
public class ServicioRol extends Auditor {

    @ManyToOne
    @JoinColumn(name = "rol", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "servicio", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Servicio servicio;

    public ServicioRol(Rol rol, Servicio servicio) {
        this.rol = rol;
        this.servicio = servicio;
    }

    public ServicioRol(Long id) {
        super(id);
    }

    public ServicioRol() {
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}
