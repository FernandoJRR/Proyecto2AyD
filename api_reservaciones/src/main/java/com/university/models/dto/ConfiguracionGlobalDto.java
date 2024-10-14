package com.university.models.dto;

import org.springframework.stereotype.Component;

@Component
public class ConfiguracionGlobalDto {

    private String nombre;

    private String imagenString = "http://localhost:8080/api/global_config/public/getImagen";

    public ConfiguracionGlobalDto() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre= nombre;
    }

    public String getImagenString() {
        return imagenString;
    }

    public void setImagenString(String imagenString) {
        this.imagenString = imagenString;
    }
}
