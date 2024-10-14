package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

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

    @OneToMany(mappedBy = "servicio", orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<ServicioRol> roles;

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

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
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

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public List<ServicioRol> getRoles() {
        return roles;
    }

    public void setRoles(List<ServicioRol> roles) {
        this.roles = roles;
    }
}
