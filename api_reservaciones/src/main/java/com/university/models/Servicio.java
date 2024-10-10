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
@Table(name = "servicio")
@DynamicUpdate
public class Servicio extends Auditor {
    @Column(name = "nombre", length = 250, unique = true)
    @NotBlank(message = "El nombre del servicio no puede estar vacío.")
    @NotNull(message = "El nombre del servicio no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre del servicio debe tener entre 1 y 250 caracteres.")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "tipo_servicio", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TipoServicio tipoServicio;

    @ManyToOne
    @JoinColumn(name = "recurso", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Recurso recurso;

    @ManyToOne
    @JoinColumn(name = "negocio", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Negocio negocio;

    public Servicio(Long id) {
        super(id);
    }

    public Servicio(String nombre, TipoServicio tipoServicio, Recurso recurso, Negocio negocio) {
        this.nombre = nombre;
        this.tipoServicio = tipoServicio;
        this.recurso = recurso;
        this.negocio = negocio;
    }

    public Servicio() {
    }
}
