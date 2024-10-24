package com.university.models;

import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "rol")
public class Rol extends Auditor {
    @Column(name = "rol", length = 250, unique = true)
    @NotBlank(message = "El nombre del rol no puede estar vacío.")
    @NotNull(message = "El nombre del rol no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre del rol debe tener entre 1 y 250 caracteres.")
    private String nombre;

    @OneToMany(mappedBy = "rol", orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(hidden = true)
    private List<UsuarioRol> asignaciones;

    @OneToMany(mappedBy = "rol", orphanRemoval = true, fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<PermisoRol> permisos;

    @OneToMany(mappedBy = "rol", orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @JsonManagedReference
    private List<ServicioRol> servicios;

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public Rol(Long id) {
        super(id);
    }

    public Rol() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<UsuarioRol> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<UsuarioRol> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public List<PermisoRol> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<PermisoRol> permisos) {
        this.permisos = permisos;
    }

    public List<ServicioRol> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioRol> servicios) {
        this.servicios = servicios;
    }

    /**
     * Metodo para mantener las relaciones de roles, permisos Para
     * evitar que se eliminen al actualizar Se debe llamar antes de actualizar
     * Se afectan todas las relaciones con orphanRemoval = true
     *
     * @param rol Es el mismo objeto que se va a actualizar
     */
    public void keepOrphanRemoval(Rol rol) {
        //this.roles = usuario.getRoles();
        this.permisos = rol.getPermisos();
        //this.facturas = usuario.getFacturas();
    }
}
