package com.university.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Permiso;
import com.university.models.PermisoRol;
import com.university.models.Rol;
import com.university.models.Servicio;
import com.university.models.ServicioRol;
import com.university.models.request.CreateRolDto;
import com.university.models.request.HorariosUsuarioRequest;
import com.university.models.request.PermisoRolRequest;
import com.university.models.request.ServicioRolRequest;
import com.university.repositories.RolRepository;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> getRoles() {
        return (List<Rol>) rolRepository.findAll();
    }

    public Rol getRol(String rolStr) throws Exception {
        Rol rol = this.rolRepository.findOneByNombre(rolStr).orElse(null);
        // si el rol no existe lanzamos error
        if (rol == null) {
            throw new Exception("Rol no encontrado.");
        }
        return rol;
    }

    public Rol getRolById(Long id_rol) throws Exception {
        Rol rol = this.rolRepository.findById(id_rol).orElse(null);
        // si el rol no existe lanzamos error
        if (rol == null) {
            throw new Exception("Rol no encontrado.");
        }
        return rol;
    }

    @Transactional
    public Rol createRol(CreateRolDto rolDto) throws Exception {
        Rol rol = rolDto.getRol();
        List<Permiso> permisosRol = rolDto.getPermisos();
        List<Servicio> serviciosRol = rolDto.getServicios();

        if (permisosRol.isEmpty()) {
            throw new Exception("Debes asignar permisos al rol");
        }
        if (serviciosRol.isEmpty()) {
            throw new Exception("Debes asignar servicios al rol");
        }

        Rol rolCreado = rolRepository.save(rol);

        //Se asignan permisos al rol
        List<PermisoRol> permisosDeRol = new ArrayList<>();
        for (Permiso permisoActual : permisosRol) {
            PermisoRol permisoRolActual = new PermisoRol(rolCreado, permisoActual);
            permisosDeRol.add(permisoRolActual);
        }
        rolCreado.setPermisos(permisosDeRol);

        //Se asignan servicios al rol
        List<ServicioRol> serviciosDeRol = new ArrayList<>();
        for (Servicio servicioActual : serviciosRol) {
            ServicioRol servicioRolActual = new ServicioRol(rolCreado, servicioActual);
            serviciosDeRol.add(servicioRolActual);
        }
        rolCreado.setServicios(serviciosDeRol);

        rolRepository.save(rol);

        return rolCreado;
    }

    public Rol updateRol(Long id, Rol producto) {
        Rol rolExistente = rolRepository.findById(id).orElse(null);
        if (rolExistente != null) {
            return rolRepository.save(rolExistente);
        }
        return null;
    }

    /**
     * Sobreescribir los permisos de un rol
     *
     * @param permisoRol
     * @return
     */
    @Transactional
    public Rol actualizarPermisosRol(PermisoRolRequest rolPermiso) throws Exception {
        // Buscamos el usuario en la base de datos
        Rol rol = this.getRolById(rolPermiso.getIdRol());

        List<PermisoRol> permisosNuevos = new ArrayList<>();
        // por cada permiso que se haya especificado creamos un nuevo permiso
        for (Permiso item : rolPermiso.getPermisos()) // Creamos el permiso
        {
            permisosNuevos.add(new PermisoRol(
                    rol, item));
        }

        if (rol.getPermisos() == null) {
            rol.setPermisos(permisosNuevos);
        } else {
            // asignamos los nuevos permisos al usuario
            rol.getPermisos().clear();
            rol.getPermisos().addAll(permisosNuevos);
        }
        // Guardamos el usuario
        return this.rolRepository.save(rol);
    }

    /**
     * Sobreescribir los servicios de un rol
     *
     * @param permisoRol
     * @return
     */
    @Transactional
    public Rol actualizarServiciosRol(ServicioRolRequest rolServicio) throws Exception {
        // Buscamos el usuario en la base de datos
        Rol rol = this.getRolById(rolServicio.getIdRol());

        List<ServicioRol> serviciosNuevos = new ArrayList<>();
        // por cada permiso que se haya especificado creamos un nuevo permiso
        for (Servicio servicioActual : rolServicio.getServicios()) // Creamos el permiso
        {
            serviciosNuevos.add(new ServicioRol(
                    rol, servicioActual));
        }

        if (rol.getServicios() == null) {
            //Si los servicios estan vacios solo se agregan
            rol.setServicios(serviciosNuevos);
        } else {
            //Sino se sobreescriben
            rol.getServicios().clear();
            rol.getServicios().addAll(serviciosNuevos);
        }
        // Guardamos el usuario
        return this.rolRepository.save(rol);
    }

    /**
     * Agregar un permiso a un usuario
     *
     * @param rol
     * @param permiso
     * @return
     */
    @Transactional
    public Rol agregarPermisoRol(Rol rol, Permiso permiso) throws Exception {
        if (!this.rolRepository.existsById(rol.getId())) {
            throw new IllegalArgumentException("El rol no existe.");
        }
        // Buscamos el usuario en la base de datos
        Optional<Rol> busquedaRol = rolRepository.findById(rol.getId());
        if (busquedaRol.isEmpty()) {
            throw new Exception("No hemos encontrado el rol.");
        }
        // Verificamos que el usuario no haya sido eliminado
        if (busquedaRol.get().getDeletedAt() != null) {
            throw new Exception("Rol ya ha sido eliminado.");
        }
        Rol usuarioEncontrado = busquedaRol.get();
        // Creamos el permiso
        PermisoRol usuarioPermiso = new PermisoRol(usuarioEncontrado, permiso);
        rol.keepOrphanRemoval(usuarioEncontrado);
        if (rol.getPermisos() == null) {
            rol.setPermisos(new ArrayList<>());
        }
        // Verificamos si el permiso ya existe
        if (rol.getPermisos().stream().anyMatch(p -> p.getPermiso().getId().equals(permiso.getId()))) {
            throw new IllegalArgumentException("El permiso ya ha sido asignado al usuario.");
        }
        // Agregamos el permiso a la lista de permisos del usuario
        rol.getPermisos().add(usuarioPermiso);
        // Guardamos el usuario
        return this.rolRepository.save(rol);
    }

    /**
     * Agregar un servicio a un rol
     *
     * @param rol
     * @param servicio
     * @return
     */
    @Transactional
    public Rol agregarServicioRol(Rol rol, Servicio servicio) throws Exception {
        if (!this.rolRepository.existsById(rol.getId())) {
            throw new IllegalArgumentException("El rol no existe.");
        }
        // Buscamos el usuario en la base de datos
        Optional<Rol> busquedaRol = rolRepository.findById(rol.getId());
        if (busquedaRol.isEmpty()) {
            throw new Exception("No hemos encontrado el rol.");
        }
        // Verificamos que el usuario no haya sido eliminado
        if (busquedaRol.get().getDeletedAt() != null) {
            throw new Exception("Rol ya ha sido eliminado.");
        }
        Rol rolEncontrado = busquedaRol.get();
        // Creamos el permiso
        ServicioRol usuarioServicio = new ServicioRol(rolEncontrado, servicio);
        rol.keepOrphanRemoval(rolEncontrado);
        if (rol.getServicios() == null) {
            rol.setServicios(new ArrayList<>());
        }
        // Verificamos si el permiso ya existe
        if (rol.getServicios().stream().anyMatch(p -> p.getServicio().getId().equals(servicio.getId()))) {
            throw new IllegalArgumentException("El servicio ya ha sido asignado al usuario.");
        }
        // Agregamos el permiso a la lista de permisos del usuario
        rol.getServicios().add(usuarioServicio);
        // Guardamos el usuario
        return this.rolRepository.save(rol);
    }
}
