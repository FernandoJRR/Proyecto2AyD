package com.university.models.dto;

import org.springframework.stereotype.Component;

import com.university.models.Usuario;

@Component
public class LoginDto {

    private Usuario usuario;
    private String jwt;
    private boolean hasTwoFactorCode;

    public LoginDto(Usuario usuario, String jwt) {
        this.usuario = usuario;
        this.jwt = jwt;
    }

    public LoginDto(Usuario usuario, String jwt, boolean hasTwoFactorCode) {
        this.usuario = usuario;
        this.jwt = jwt;
        this.hasTwoFactorCode = hasTwoFactorCode;
    }

    public LoginDto() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public boolean isHasTwoFactorCode() {
        return hasTwoFactorCode;
    }

    public void setHasTwoFactorCode(boolean hasTwoFactorCode) {
        this.hasTwoFactorCode = hasTwoFactorCode;
    }
}
