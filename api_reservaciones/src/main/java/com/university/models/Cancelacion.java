package com.university.models;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cancelacion")
public class Cancelacion extends Auditor {

    @OneToOne
    @JoinColumn(name = "reservacion", nullable = false)
    private Reservacion reservacion;

    @Column(name = "fecha_cancelacion", nullable = false)
    private LocalDate fechaCancelacion;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "monto_reembolsado", nullable = false, precision = 10, scale = 2)
    private Float montoReembolsado;

    public Cancelacion() {
    }

    public Cancelacion(Reservacion reservacion, LocalDate fechaCancelacion, String motivoCancelacion, Float montoReembolsado) {
        this.reservacion = reservacion;
        this.fechaCancelacion = fechaCancelacion;
        this.motivoCancelacion = motivoCancelacion;
        this.montoReembolsado = montoReembolsado;
    }

    public Reservacion getReservacion() {
        return reservacion;
    }

    public void setReservacion(Reservacion reservacion) {
        this.reservacion = reservacion;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public Float getMontoReembolsado() {
        return montoReembolsado;
    }

    public void setMontoReembolsado(Float montoReembolsado) {
        this.montoReembolsado = montoReembolsado;
    }
}
