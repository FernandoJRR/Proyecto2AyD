package com.university.models.request;

import java.util.List;

import org.springframework.stereotype.Component;

import com.university.models.HorarioAtencionUsuario;

@Component
public class HorariosUsuarioRequest {

    private String emailUsuario;
    private List<HorarioAtencionUsuario> horariosAtencionUsuario;

    public HorariosUsuarioRequest(String emailUsuario, List<HorarioAtencionUsuario> horarioAtencionUsuarios) {
        this.emailUsuario = emailUsuario;
        this.horariosAtencionUsuario = horarioAtencionUsuarios;
    }

    public HorariosUsuarioRequest() {
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public List<HorarioAtencionUsuario> getHorariosAtencionUsuario() {
        return horariosAtencionUsuario;
    }

    public void setHorariosAtencionUsuario(List<HorarioAtencionUsuario> horariosAtencionUsuario) {
        this.horariosAtencionUsuario = horariosAtencionUsuario;
    }
}
