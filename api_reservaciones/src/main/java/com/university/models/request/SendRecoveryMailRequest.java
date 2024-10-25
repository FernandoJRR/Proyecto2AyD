package com.university.models.request;

import org.springframework.stereotype.Component;

@Component
public class SendRecoveryMailRequest {

    private String correoElectronico;

    public SendRecoveryMailRequest(){}

    public SendRecoveryMailRequest(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}
