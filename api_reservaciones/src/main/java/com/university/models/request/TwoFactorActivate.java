package com.university.models.request;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component
public class TwoFactorActivate {

    @NotNull(message = "El id no puede ser nulo.")
    private Long id;
    @NotNull(message = "El estado de activacion no puede ser nulo.")
    private Boolean activacion;

    public TwoFactorActivate(Long id, Boolean activacion) {
        this.id = id;
        this.activacion = activacion;
    }

    public TwoFactorActivate() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActivacion() {
        return activacion;
    }

    public void setActivacion(Boolean activacion) {
        this.activacion = activacion;
    }
}
