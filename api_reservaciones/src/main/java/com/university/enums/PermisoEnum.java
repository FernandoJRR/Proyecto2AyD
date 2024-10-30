package com.university.enums;

public enum PermisoEnum {
    ADMIN("ADMINISTRADOR", "admin"),
    USUARIO_VER("USUARIO_VER", "usuario_ver"),
    USUARIO_CREAR("USUARIO_CREAR", "usuario_crear"),
    USUARIO_MODIFICAR("USUARIO_MODIFICAR", "usuario_modificar"),
    ROL_VER("ROL_VER", "rol_ver"),
    ROL_CREAR("ROL_CREAR", "rol_crear"),
    ROL_MODIFICAR("ROL_MODIFICAR", "rol_modificar"),
    NEGOCIO_VER("NEGOCIO_VER", "negocio_ver"),
    NEGOCIO_CREAR("NEGOCIO_CREAR", "negocio_crear"),
    NEGOCIO_MODIFICAR("NEGOCIO_MODIFICAR", "negocio_modificar"),
    SERVICIO_VER("SERVICIO_VER", "servicio_ver"),
    SERVICIO_CREAR("SERVICIO_CREAR", "servicio_crear"),
    SERVICIO_MODIFICAR("SERVICIO_MODIFICAR", "servicio_modificar"),
    RECURSO_VER("RECURSO_VER", "recurso_ver"),
    RECURSO_CREAR("RECURSO_CREAR", "recurso_crear"),
    RECURSO_MODIFICAR("RECURSO_MODIFICAR", "recurso_modificar"),
    UNIDAD_RECURSO_VER("UNIDAD_RECURSO_VER", "unidad_recurso_ver"),
    UNIDAD_RECURSO_CREAR("UNIDAD_RECURSO_CREAR", "unidad_recurso_crear"),
    UNIDAD_RECURSO_MODIFICAR("UNIDAD_RECURSO_MODIFICAR", "unidad_recurso_modificar"),
    RESERVACION_VER("RESERVACION_VER", "reservacion_ver"),
    RESERVACION_MODIFICAR("RESERVACION_MODIFICAR", "reservacion_modificar"),
    REPORTES("REPORTES", "reportes"),

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
