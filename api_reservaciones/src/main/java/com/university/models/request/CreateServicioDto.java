package com.university.models.request;

import org.springframework.stereotype.Component;

import com.university.models.DuracionServicio;
import com.university.models.HorarioAtencionServicio;
import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Servicio;

import java.util.List;

@Component
public class CreateServicioDto {

    private Servicio servicio;
    private DuracionServicio duracionServicio;
    private List<HorarioAtencionServicio> horariosAtencionServicio;

    public CreateServicioDto(Servicio servicio,
            DuracionServicio duracionServicio,
            List<HorarioAtencionServicio> horariosAtencionServicio) {
        this.servicio = servicio;
        this.duracionServicio = duracionServicio;
        this.horariosAtencionServicio = horariosAtencionServicio;
    }

    public CreateServicioDto() {
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public DuracionServicio getDuracionServicio() {
        return duracionServicio;
    }

    public void setDuracionServicio(DuracionServicio duracionServicio) {
        this.duracionServicio = duracionServicio;
    }

    public List<HorarioAtencionServicio> getHorariosAtencionServicio() {
        return horariosAtencionServicio;
    }

    public void setHorariosAtencionServicio(List<HorarioAtencionServicio> horariosAtencionServicio) {
        this.horariosAtencionServicio = horariosAtencionServicio;
    }
}
