package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "recurso")
@DynamicUpdate
public class Recurso extends Auditor {
    @Column(name = "nombre", length = 250, unique = true)
    @NotBlank(message = "El nombre del recurso no puede estar vacío.")
    @NotNull(message = "El nombre del recurso no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre del recurso debe tener entre 1 y 250 caracteres.")
    private String nombre;

    public Recurso(String nombre) {
        this.nombre = nombre;
    }

    public Recurso(Long id) {
        super(id);
    }

    public Recurso() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
