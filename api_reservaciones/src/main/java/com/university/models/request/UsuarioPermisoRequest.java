package com.university.models.request;

import java.util.List;

import org.springframework.stereotype.Component;

import com.university.models.Permiso;

@Component
public class UsuarioPermisoRequest {

    private Long idUsuario;
    private List<Permiso> permisos;

    public UsuarioPermisoRequest(Long idUsuario, List<Permiso> permisos) {
        this.idUsuario = idUsuario;
        this.permisos = permisos;
    }

    public UsuarioPermisoRequest() {
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }
}
