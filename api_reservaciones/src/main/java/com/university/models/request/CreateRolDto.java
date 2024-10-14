package com.university.models.request;

import org.springframework.stereotype.Component;

import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Servicio;

import java.util.List;

@Component
public class CreateRolDto {

    private Rol rol;
    private List<Permiso> permisos;
    private List<Servicio> servicios;

    public CreateRolDto(Rol rol, List<Permiso> permisos, List<Servicio> servicios) {
        this.rol = rol;
        this.permisos = permisos;
        this.servicios = servicios;
    }

    public CreateRolDto() {
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }
}
