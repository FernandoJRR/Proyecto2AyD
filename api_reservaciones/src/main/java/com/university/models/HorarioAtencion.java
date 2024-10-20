package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalTime;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "horario_atencion")
@DynamicUpdate
public class HorarioAtencion extends Auditor {
  @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_final", nullable = false)
    private LocalTime horaFinal;

    public HorarioAtencion(LocalTime horaInicio, LocalTime horaFinal) {
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
    }

    public HorarioAtencion() {
    }

}
