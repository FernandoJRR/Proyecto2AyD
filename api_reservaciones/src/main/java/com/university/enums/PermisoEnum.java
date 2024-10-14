package com.university.enums;

public enum PermisoEnum {
    ADMIN("Administrador", "admin"),
    USUARIO_CREAR("Crear usuarios con diferentes roles", "usuario_crear"),
    USUARIO_MODIFICAR("Modificar usuarios con roles", "usuario_modificar"),
    ROL_CREAR("Crear roles para usuarios", "rol_crear"),
    ROL_MODIFICAR("Modificar roles para usuarios", "rol_modificar"),
    USUARIO("Usuario","usuario");

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
