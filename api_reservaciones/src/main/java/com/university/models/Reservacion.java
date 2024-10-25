package com.university.models;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "reservacion")
@DynamicUpdate
public class Reservacion extends Auditor {

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "encargado", nullable = false)
    private Usuario encargado;

    @ManyToOne
    @JoinColumn(name = "servicio", nullable = false)
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "id_unidad_recurso", nullable = true)
    private UnidadRecurso unidadRecurso;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_final", nullable = false)
    private LocalTime horaFinal;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "pago", nullable = true)
    private Pago pago;

    @ManyToOne
    @JoinColumn(name = "estado_reservacion", nullable = false)
    private EstadoReservacion estadoReservacion;

    public Reservacion() {
    }

    public Reservacion(Usuario usuario, Usuario encargado, Servicio servicio, UnidadRecurso unidadRecurso,
                       LocalTime horaInicio, LocalTime horaFinal, LocalDate fecha, Pago pago,
                       EstadoReservacion estadoReservacion) {
        this.usuario = usuario;
        this.encargado = encargado;
        this.servicio = servicio;
        this.unidadRecurso = unidadRecurso;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.fecha = fecha;
        this.pago = pago;
        this.estadoReservacion = estadoReservacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getEncargado() {
        return encargado;
    }

    public void setEncargado(Usuario encargado) {
        this.encargado = encargado;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public UnidadRecurso getUnidadRecurso() {
        return unidadRecurso;
    }

    public void setUnidadRecurso(UnidadRecurso unidadRecurso) {
        this.unidadRecurso = unidadRecurso;
    }

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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public EstadoReservacion getEstadoReservacion() {
        return estadoReservacion;
    }

    public void setEstadoReservacion(EstadoReservacion estadoReservacion) {
        this.estadoReservacion = estadoReservacion;
    }
}
