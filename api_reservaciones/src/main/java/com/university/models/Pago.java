package com.university.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "pago")
@DynamicUpdate
public class Pago extends Auditor {

    @ManyToOne
    @JoinColumn(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "numero", nullable = false, length = 100)
    private String numero;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    public Pago() {
    }

    public Pago(MetodoPago metodoPago, String numero, BigDecimal monto) {
        this.metodoPago = metodoPago;
        this.numero = numero;
        this.monto = monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
