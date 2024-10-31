package com.university.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "tipo_servicio")
@DynamicUpdate
public class TipoServicio extends Auditor {
    @Column(name = "nombre", length = 250, unique = true)
    @NotBlank(message = "El nombre del tipo de servicio no puede estar vacío.")
    @NotNull(message = "El nombre del tipo de servicio no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre del tipo de servicio debe tener entre 1 y 250 caracteres.")
    private String nombre;

    public TipoServicio(String nombre) {
        this.nombre = nombre;
    }

    public TipoServicio(Long id) {
        super(id);
    }

    public TipoServicio() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
