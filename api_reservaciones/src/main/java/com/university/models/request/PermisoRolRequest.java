package com.university.models.request;

import java.util.List;

import org.springframework.stereotype.Component;

import com.university.models.Permiso;

@Component
public class PermisoRolRequest {

    private Long idRol;
    private List<Permiso> permisos;

    public PermisoRolRequest(Long idRol, List<Permiso> permisos) {
        this.idRol = idRol;
        this.permisos = permisos;
    }

    public PermisoRolRequest() {
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idUsuario) {
        this.idRol = idUsuario;
    }

    public List<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }
}
