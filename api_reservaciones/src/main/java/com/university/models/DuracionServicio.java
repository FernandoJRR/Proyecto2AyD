package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "duracion_servicio")
@DynamicUpdate
public class DuracionServicio extends Auditor {
    @ManyToOne
    @JoinColumn(name = "servicio", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Servicio servicio;

    @Column(name = "minutos", length = 250)
    @NotBlank(message = "Los minutos de la duracion no puede ser vacio.")
    @NotNull(message = "Los minutos de la duracion no puede ser nulo.")
    @Min(value = 0, message = "La cantidad de minutos no puede ser negativa.")
    @Max(value = 59, message = "La cantidad de minutos no puede ser mayor a 59.")
    private Integer minutos;

    @Column(name = "horas", length = 250)
    @NotBlank(message = "Las horas de la duracion no puede ser vacio.")
    @NotNull(message = "Las horas de la duracion no puede ser nulo.")
    @Min(value = 0, message = "La cantidad de horas no puede ser negativa.")
    @Max(value = 10, message = "La cantidad de horas no puede ser mayor a 10.")
    private Integer horas;

    public DuracionServicio(Servicio servicio, Integer minutos, Integer horas) {
        this.servicio = servicio;
        this.minutos = minutos;
        this.horas = horas;
    }

    public DuracionServicio(Integer minutos, Integer horas) {
        this.minutos = minutos;
        this.horas = horas;
    }

    public DuracionServicio(Long id) {
        super(id);
    }

    public DuracionServicio() {
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Integer getMinutos() {
        return minutos;
    }

    public void setMinutos(Integer minutos) {
        this.minutos = minutos;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }
}
