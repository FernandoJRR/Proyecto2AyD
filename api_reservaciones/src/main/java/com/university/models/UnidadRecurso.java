package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "unidad_recurso")
@DynamicUpdate
public class UnidadRecurso extends Auditor {
    @Column(name = "nombre", length = 250, unique = true)
    @NotBlank(message = "El nombre de la unidad de recurso no puede estar vacío.")
    @NotNull(message = "El nombre de la unidad de recurso no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre de la unidad de recurso debe tener entre 1 y 250 caracteres.")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "recurso", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Recurso recurso;

    public UnidadRecurso(String nombre, Recurso recurso) {
        this.nombre = nombre;
        this.recurso = recurso;
    }

    public UnidadRecurso(Long id) {
        super(id);
    }

    public UnidadRecurso() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }
}
