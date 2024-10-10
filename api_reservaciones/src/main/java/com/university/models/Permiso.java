package com.university.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "permiso")
@DynamicUpdate
public class Permiso extends Auditor {

    @Column(name = "nombre", length = 250, unique = true)
    @NotBlank(message = "El nombre del permiso no puede estar vacío.")
    @NotNull(message = "El nombre del permiso no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre del permiso debe tener entre 1 y 250 caracteres.")
    private String nombre;

    @Column(name = "ruta", length = 250, unique = true)
    @NotBlank(message = "El nombre del permiso no puede estar vacío.")
    @NotNull(message = "El nombre del permiso no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre del permiso debe tener entre 1 y 250 caracteres.")
    private String ruta;

    @OneToMany(mappedBy = "permiso", orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(hidden = true)
    private List<UsuarioPermiso> asignaciones;

    public Permiso(String nombre, String ruta) {
        this.nombre = nombre;
        this.ruta = ruta;
    }

    public Permiso(Long id) {
        super(id);
    }

    public Permiso() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<UsuarioPermiso> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<UsuarioPermiso> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
