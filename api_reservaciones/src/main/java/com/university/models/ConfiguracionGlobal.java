package com.university.models;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    public ConfiguracionGlobal(String nombre, byte[] imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public ConfiguracionGlobal() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
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
