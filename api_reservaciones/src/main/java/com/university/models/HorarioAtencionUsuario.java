package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

@Entity
@Table(name = "horario_atencion_usuario")
public class HorarioAtencionUsuario extends Auditor {

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_final", nullable = false)
    private LocalTime horaFinal;

    @ManyToOne
    @JoinColumn(name = "dia_atencion", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DiaAtencion diaAtencion;

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Cascade(CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuario;

    public HorarioAtencionUsuario(LocalTime horaInicio, LocalTime horaFinal, DiaAtencion diaAtencion, Usuario usuario) {
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.diaAtencion = diaAtencion;
        this.usuario = usuario;
    }

    public HorarioAtencionUsuario(LocalTime horaInicio, LocalTime horaFinal, DiaAtencion diaAtencion) {
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.diaAtencion = diaAtencion;
    }

    public HorarioAtencionUsuario() {}

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(LocalTime horaFinal) {
        this.horaFinal = horaFinal;
    }

    public DiaAtencion getDiaAtencion() {
        return diaAtencion;
    }

    public void setDiaAtencion(DiaAtencion diaAtencion) {
        this.diaAtencion = diaAtencion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
