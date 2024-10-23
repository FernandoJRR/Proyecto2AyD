package com.university.models.request;

import org.springframework.stereotype.Component;

import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Servicio;
import com.university.models.Usuario;

import java.util.List;

@Component
public class CreateUsuarioDto {

    private Usuario usuario;
    private List<Rol> roles;

    public CreateUsuarioDto(Usuario usuario, List<Rol> roles) {
        this.usuario = usuario;
        this.roles = roles;
    }

    public CreateUsuarioDto() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }
}
