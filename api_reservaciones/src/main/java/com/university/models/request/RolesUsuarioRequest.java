package com.university.models.request;

import java.util.List;

import org.springframework.stereotype.Component;

import com.university.models.Rol;

@Component
public class RolesUsuarioRequest {

    private String emailUsuario;
    private List<Rol> rolesUsuario;

    public RolesUsuarioRequest(String emailUsuario, List<Rol> roles) {
        this.emailUsuario = emailUsuario;
        this.rolesUsuario = roles;
    }

    public RolesUsuarioRequest() {
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public List<Rol> getRolesUsuario() {
        return rolesUsuario;
    }

    public void setRolesUsuario(List<Rol> rolesUsuario) {
        this.rolesUsuario = rolesUsuario;
    }
}
