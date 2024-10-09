package com.university.models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "permiso_usuario")
public class UsuarioPermiso extends Auditor {

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(hidden = true)
    private Usuario usuario;

    @ManyToOne//indicador de relacion muchos a uno
    @JoinColumn(name = "permiso", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Permiso permiso;

    public UsuarioPermiso(Long id) {
        super(id);
    }

    public UsuarioPermiso(Usuario usuario, Permiso permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
    }

    public UsuarioPermiso() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }

    @Override
    public String toString() {
        return permiso.getNombre();
    }
}
