package com.university.models.request;

import java.util.List;

import org.springframework.stereotype.Component;

import com.university.models.Servicio;

@Component
public class ServicioRolRequest {

    private Long idRol;
    private List<Servicio> servicios;

    public ServicioRolRequest(Long idRol, List<Servicio> servicios) {
        this.idRol = idRol;
        this.servicios = servicios;
    }

    public ServicioRolRequest() {
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idUsuario) {
        this.idRol = idUsuario;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }
}
