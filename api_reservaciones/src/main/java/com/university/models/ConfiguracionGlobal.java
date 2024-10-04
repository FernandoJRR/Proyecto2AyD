package com.university.models;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "configuracion_global")
@DynamicUpdate
public class ConfiguracionGlobal extends Auditor{

    @Column(name = "nombre", length = 250, unique = false)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 1, max = 250, message = "El nombre debe tener entre 1 y 250 caracteres.")
    private String nombre;

    @Column(name = "imagen", nullable = false, length = Integer.MAX_VALUE)
    @Lob
    private byte[] imagen;

    @Column(length = 250)
    private String mimeTypeImg;

    public ConfiguracionGlobal(Long id) {
        super(id);
    }

    public ConfiguracionGlobal(String nombreTienda, byte[] imagenTienda) {
        this.nombre = nombreTienda;
        this.imagen = imagenTienda;
    }

    public ConfiguracionGlobal() {
    }

    public String getNombreTienda() {
        return nombre;
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombre = nombreTienda;
    }

    public byte[] getImagenTienda() {
        return imagen;
    }

    public void setImagenTienda(byte[] imagenTienda) {
        this.imagen = imagenTienda;
    }

    public String getMimeTypeImg() {
        return mimeTypeImg;
    }

    public void setMimeTypeImg(String mimeTypeImg) {
        this.mimeTypeImg = mimeTypeImg;
    }

    public String getExtension() {
        if (this.mimeTypeImg.split("/")[1] != null) {
            return this.mimeTypeImg.split("/")[1];
        }
        return "png";
    }
}
