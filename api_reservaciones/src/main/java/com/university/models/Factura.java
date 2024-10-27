package com.university.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "factura")
public class Factura extends Auditor {

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "reservacion", nullable = false)
    private Reservacion reservacion;

    @Column(name = "monto", nullable = false)
    private Float monto;

    public Factura() {
    }

    public Factura(Usuario usuario, Reservacion reservacion, Float monto) {
        this.usuario = usuario;
        this.reservacion = reservacion;
        this.monto = monto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Reservacion getReservacion() {
        return reservacion;
    }

    public void setReservacion(Reservacion reservacion) {
        this.reservacion = reservacion;
    }

    public Float getMonto() {
        return monto;
    }

    public void setMonto(Float monto) {
        this.monto = monto;
    }
}
