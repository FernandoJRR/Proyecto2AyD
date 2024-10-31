package com.university.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.LocalDate;

@Component
public class CreateReservacionDto {
    @NotBlank(message = "El email del usuario es requerido.")
    private String emailUsuario;

    private Long idEncargado;

    @NotNull(message = "El ID del servicio es requerido.")
    private Long idServicio;

    private String nombreUnidadRecurso;
    private String nombreRecurso;

    @NotNull(message = "La hora de inicio es requerida.")
    private LocalTime horaInicio;
    @NotNull(message = "La hora final es requerida.")
    private LocalTime horaFinal;
    @NotNull(message = "La fecha de la reservación es requerida.")
    private LocalDate fecha;

    @NotBlank(message = "El nombre del método de pago es requerido.")
    private String nombreMetodoPago;
    @NotBlank(message = "El número de pago es requerido.")
    private String numeroPago;
    @NotNull(message = "El monto del pago es requerido.")
    private Float montoPago;

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public Long getIdEncargado() {
        return idEncargado;
    }

    public void setIdEncargado(Long idEncargado) {
        this.idEncargado = idEncargado;
    }

    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreUnidadRecurso() {
        return nombreUnidadRecurso;
    }

    public void setNombreUnidadRecurso(String nombreUnidadRecurso) {
        this.nombreUnidadRecurso = nombreUnidadRecurso;
    }

    public String getNombreRecurso() {
        return nombreRecurso;
    }
    public void setNombreRecurso(String nombreRecurso) {
        this.nombreRecurso = nombreRecurso;
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

    public String getNombreMetodoPago() {
        return nombreMetodoPago;
    }

    public void setNombreMetodoPago(String nombreMetodoPago) {
        this.nombreMetodoPago = nombreMetodoPago;
    }

    public String getNumeroPago() {
        return numeroPago;
    }

    public void setNumeroPago(String numeroPago) {
        this.numeroPago = numeroPago;
    }

    public Float getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(Float montoPago) {
        this.montoPago = montoPago;
    }
}
