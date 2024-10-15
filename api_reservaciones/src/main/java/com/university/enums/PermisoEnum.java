package com.university.enums;

public enum PermisoEnum {
    ADMIN("ADMINISTRADOR", "admin"),
    USUARIO_CREAR("USUARIO_CREAR", "usuario_crear"),
    USUARIO_MODIFICAR("USUARIO_MODIFICAR", "usuario_modificar"),
    ROL_CREAR("ROL_CREAR", "rol_crear"),
    ROL_MODIFICAR("ROL_MODIFICAR", "rol_modificar"),
    NEGOCIO_CREAR("NEGOCIO_CREAR", "negocio_crear"),
    NEGOCIO_MODIFICAR("NEGOCIO_MODIFICAR", "negocio_modificar"),
    USUARIO("USUARIO","usuario");

    private final String nombrePermiso;
    private final String ruta;

    private PermisoEnum(String nombrePermiso, String ruta) {
            this.nombrePermiso = nombrePermiso;
            this.ruta = ruta;
    }

    @Override
    public String toString() {
            return this.ruta;
    }

    public String getRuta() {
            return this.ruta;
    }

    public String getNombrePermiso() {
            return nombrePermiso;
    }
}
