package com.university.models.request;

import org.springframework.stereotype.Component;

@Component
public class VerifyUserRequest {

    private String codigoVerificacion;

    public VerifyUserRequest(){}

    public VerifyUserRequest(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }
}
