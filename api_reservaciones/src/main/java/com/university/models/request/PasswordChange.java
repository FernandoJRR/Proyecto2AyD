package com.university.models.request;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Component
public class PasswordChange {

    @Size(min = 1, max = 250, message = "La contraseña debe tener entre 1 y 250 caracteres.")
    @NotBlank(message = "La contraseña no puede estar vacía.")
    @NotNull(message = "La contraseña no puede ser nula.")
    private String nuevaPassword;
    @NotBlank(message = "La contraseña no puede estar vacía.")
    @NotNull(message = "La contraseña no puede ser nula.")
    private String codigo;

    public PasswordChange(String nuevaPassword, String codigo) {
        this.nuevaPassword = nuevaPassword;
        this.codigo = codigo;
    }

    public PasswordChange() {
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
