package com.university.models.request;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class CreateCancelacionDto {
    private Long idReservacion;
    private String motivoCancelacion;
    private LocalDate fechaCancelacion;

    // Getters y Setters
    public Long getIdReservacion() {
        return idReservacion;
    }

    public void setIdReservacion(Long idReservacion) {
        this.idReservacion = idReservacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }
}
