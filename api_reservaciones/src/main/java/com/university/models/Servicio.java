package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @OneToOne(mappedBy = "servicio", orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private DuracionServicio duracionServicio;

    @OneToMany(mappedBy = "servicio", orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<ServicioRol> roles;

    @Column(name = "costo", length = 250)
    @NotBlank(message = "El costo del servicio no puede ser vacio.")
    @NotNull(message = "El costo del servicio no puede ser nulo.")
    @Min(value = 0, message = "El costo del servicio no puede ser negativo.")
    private Float costo;

    @Column(name = "dias_cancelacion")
    @NotBlank(message = "Los dias maximos de cancelacion del servicio no puede ser vacio.")
    @NotNull(message = "Los dias maximos de cancelacion del servicio no puede ser nulo.")
    @Min(value = 0, message = "Los dias maximos de cancelacion del servicio no puede ser negativo.")
    private Integer dias_cancelacion;

    @Column(name = "porcentaje_reembolso")
    @NotBlank(message = "El porcentaje de reembolso del servicio no puede ser vacio.")
    @NotNull(message = "El porcentaje de reembolso del servicio no puede ser nulo.")
    @Min(value = 0, message = "El porcentaje de reembolso del servicio no puede ser negativo.")
    @Max(value = 100, message = "El porcentaje de reembolso del servicio no puede ser mayor al 100%.")
    private Float porcentaje_reembolso;

    public Servicio(Long id) {
        super(id);
    }

    public Servicio(String nombre, TipoServicio tipoServicio, Recurso recurso, Negocio negocio) {
        this.nombre = nombre;
        this.tipoServicio = tipoServicio;
        this.recurso = recurso;
        this.negocio = negocio;
    }

    public Servicio(String nombre, TipoServicio tipoServicio, Negocio negocio) {
        this.nombre = nombre;
        this.tipoServicio = tipoServicio;
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

    public DuracionServicio getDuracionServicio() {
        return duracionServicio;
    }

    public void setDuracionServicio(DuracionServicio duracionServicio) {
        this.duracionServicio = duracionServicio;
    }

    public List<ServicioRol> getRoles() {
        return roles;
    }

    public void setRoles(List<ServicioRol> roles) {
        this.roles = roles;
    }
}
