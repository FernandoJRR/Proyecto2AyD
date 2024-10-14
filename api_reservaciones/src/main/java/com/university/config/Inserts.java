package com.university.config;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.university.enums.PermisoEnum;
import com.university.models.ConfiguracionGlobal;
import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Usuario;
import com.university.repositories.ConfiguracionGlobalRepository;
import com.university.repositories.PermisoRepository;
import com.university.repositories.RolRepository;
import com.university.services.RolService;
import com.university.services.UsuarioService;

@Component
public class Inserts implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PermisoRepository permisoRepository;
    @Autowired
    private ConfiguracionGlobalRepository configuracionGlobalRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;

    public Rol insertarRol(Rol rol) throws Exception {
        try {
            Optional<Rol> opRol = this.rolRepository.findOneByNombre(rol.getNombre());
            if (opRol.isPresent()) {
                return opRol.get();
            }
            return this.rolRepository.save(rol);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public Permiso insertarPermiso(Permiso permiso) throws Exception {
        try {
            Optional<Permiso> opPermiso = this.permisoRepository.findOneByNombre(permiso.getNombre());
            if (opPermiso.isPresent()) {
                return opPermiso.get();
            }
            return this.permisoRepository.save(permiso);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public ConfiguracionGlobal insertarConfiguracionGlobal(ConfiguracionGlobal config) throws Exception {
        try {
            ConfiguracionGlobal conf = this.configuracionGlobalRepository.findFirstByOrderByIdAsc().orElse(null);
            if (conf == null) {
                return this.configuracionGlobalRepository.save(config);
            }
            return conf;

        } catch (Exception e) {
            throw new Exception("Error");
        }
    }

    public Permiso insertarPermisoSiNoExiste(Permiso pa) {
        Permiso permiso = this.permisoRepository.findOneByNombre(pa.getNombre()).orElse(null);
        if (permiso == null) {
            return this.permisoRepository.save(
                    pa);
        }
        return permiso;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            // Roles
            this.insertarRol(new Rol("USUARIO"));
            this.insertarRol(new Rol("ADMIN"));

            // IMAGEN DEFAULT DE LA TIENDA
            byte[] img = getClass().getResourceAsStream("/img/logo.png").readAllBytes();

            ConfiguracionGlobal configuracionGlobal = new ConfiguracionGlobal("Reservaciones GT", img);
            this.insertarConfiguracionGlobal(configuracionGlobal);

            // Seeder de usuarios del sistema
            Usuario admin = new Usuario(
                "admin",
                "admin",
                "email@gmail.com",
                null,
                null,
                "12345",
                null,
                null,
                false
            );
            Usuario usuario = new Usuario(
                "Carlos",
                "Pac",
                "carlosbpac@gmail.com",
                null,
                null,
                "12345",
                null,
                null,
                false
            );

            /*
             * Crear los usuarios, un catch por usuario para que ignore las
             * excepciones que puedan haber
             */
            try {
                this.usuarioService.crearAdministrador(admin);
            } catch (Exception e) {
            }

            try {
                this.usuarioService.crearUsuarioNormal(usuario);
            } catch (Exception e) {
            }

            // Creacion de todos los permisos que tiene el sistema
            for (PermisoEnum permiso : PermisoEnum.values()) {
                //Se crea el permiso si no existe
                Permiso permisoInsercion = this.insertarPermiso(
                    new Permiso(
                        permiso.getNombrePermiso(),
                        permiso.getRuta()
                    )
                );

                System.out.println("permiso");
                System.out.println(permisoInsercion.getNombre());
                //Si el permiso es ADMIN se asigna al mismo rol
                if (permisoInsercion.getNombre().equals("Administrador")) {
                    System.out.println("insertando permiso");
                    Rol rolAdmin = this.rolService.getRol("ADMIN");
                    this.rolService.agregarPermisoRol(rolAdmin, permisoInsercion);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Inserts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
