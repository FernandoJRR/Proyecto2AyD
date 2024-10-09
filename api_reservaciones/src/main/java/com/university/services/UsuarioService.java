package com.university.services;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Usuario;
import com.university.models.UsuarioPermiso;
import com.university.models.UsuarioRol;
import com.university.models.dto.LoginDto;
import com.university.models.request.PasswordChange;
import com.university.models.request.UsuarioPermisoRequest;
import com.university.repositories.UsuarioRepository;
import com.university.repositories.UsuarioRolRepository;
import com.university.services.authentication.AuthenticationService;
import com.university.services.authentication.JwtGeneratorService;
import com.university.tools.Encriptador;
import com.university.tools.MailService;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService extends com.university.services.Service {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolService rolService;
    @Autowired
    private Encriptador encriptador;
    @Autowired
    private MailService mailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UsuarioRolRepository usuarioRolRepository;
    @Autowired
    private JwtGeneratorService jwtGenerator;

    public List<Usuario> getUsuarios() {
        return this.ignorarEliminados(usuarioRepository.findAll());
    }

    public Usuario getByEmail(String email) throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado.");
        }
        if (usuario.getDeletedAt() != null) {
            throw new Exception("Usuario eliminado.");
        }
        return usuario;
    }

    public Usuario getUsuario(Long id) throws Exception {
        if (id == null || id <= 0) {// si el correo esta en blanco entonces lanzmaos error
            throw new Exception("Id invalido.");
        }

        // mandamos a traer el estado de la cuenta
        Optional<Usuario> busquedaUsuario = usuarioRepository.findById(id);

        if (busquedaUsuario.isEmpty()) {
            throw new Exception("No hemos encontrado el usuario.");
        }

        Usuario usuarioEncontrado = busquedaUsuario.get();

        // vemos si el usuario no ha sido eliminado
        if (usuarioEncontrado.getDeletedAt() != null) {
            throw new Exception("Usuario ya ha sido eliminado.");
        }

        return usuarioEncontrado;
    }

    @Transactional
    public String eliminarUsuario(Long id, String emailUsuarioAutenticado) throws Exception {

        if (id == null || id <= 0) {// si el correo esta en blanco entonces lanzmaos error
            throw new Exception("Id invalido.");
        }

        // mandamos a traer el estado de la cuenta
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        // si esta vacio entonces el usuairo no existe
        if (usuario == null) {
            throw new Exception("No hemos encontrado el usuario.");
        }

        // validar si el usuario tiene permiso de eliminar
        this.verificarUsuarioJwt(usuario, emailUsuarioAutenticado);

        // editar el usuario
        Long eliminar = this.usuarioRepository.deleteUsuarioById(usuario.getId());

        // mandamos a editar la password y comparamos si se hizo el cambio
        if (eliminar > 0) {
            return "Se elimino el usuario con exito.";
        }
        throw new Exception("No pudimos eliminar el usuario, inténtalo más tarde.");
    }

    @Transactional
    public Usuario updateUsuario(Usuario usuario, String emailUsuarioAutenticado) throws Exception {
        if (usuario.getId() == null || usuario.getId() <= 0) {
            throw new Exception("Id inválido.");
        }

        Optional<Usuario> busquedaUsuario = usuarioRepository.findById(usuario.getId());
        if (busquedaUsuario.isEmpty()) {
            throw new Exception("No hemos encontrado el usuario.");
        }
        Usuario usuarioEncontrado = busquedaUsuario.get();

        // validar si el usuario tiene permiso de eliminar
        this.verificarUsuarioJwt(usuarioEncontrado, emailUsuarioAutenticado);

        // vemos si el usuario no ha sido eliminado
        if (usuarioEncontrado.getDeletedAt() != null) {
            throw new Exception("Usuario ya ha sido eliminado.");
        }
        // vemos que no exista otro con el mismo email
        if (this.usuarioRepository.existsUsuarioByEmailAndIdNot(usuario.getEmail(),
                usuario.getId())) {
            // si el metodo no se rompe hubo un error insesperado
            throw new Exception(String.format("No se editó el usuario %s, "
                    + "debido a que ya existe otro usuario con el mismo email.",
                    usuario.getEmail()));
        }
        // Evitar el cambio de contraseña
        usuario.setPassword(usuarioEncontrado.getPassword());
        //usuario.setFacturas(usuarioEncontrado.getFacturas());
        usuario.setRoles(usuarioEncontrado.getRoles());
        usuario.setPermisos(usuarioEncontrado.getPermisos());
        this.validar(usuario);
        Usuario usuarioUpdate = this.usuarioRepository.save(usuario);
        // Verificar si la actualización falló
        if (usuarioUpdate == null || usuarioUpdate.getId() <= 0) {
            throw new Exception("No pudimos actualizar el usuario, inténtalo más tarde.");
        }
        return usuarioUpdate;
    }

    public String enviarMailDeRecuperacion(String correo) throws Exception {
        if (correo.isBlank()) {// si el correo esta en blanco entonces lanzmaos error
            throw new Exception("Correo vacio.");
        }

        // mandamos a traer el estado de la cuenta
        Optional<Usuario> busquedaUsuario = usuarioRepository.findByEmail(correo);
        if (busquedaUsuario.isEmpty()) {
            throw new Exception("No hemos encontrado tu correo electrónico.");
        }
        // obtenemos el modelo
        Usuario usuario = busquedaUsuario.get();
        // vemos si el usuario no ha sido eliminado
        if (usuario.getDeletedAt() != null) {
            throw new Exception("Usuario ya ha sido eliminado.");
        }
        // creamos el codigo de recuperacion
        String codigoRecuperacion = UUID.randomUUID().toString();
        // actualizamos el codigo de recuperacion
        usuario.setCodigoRecuperacion(codigoRecuperacion);
        // actualizamos en la bd
        Usuario actualizacion = usuarioRepository.save(usuario);

        if (actualizacion.getCodigoRecuperacion() == null || !actualizacion.getCodigoRecuperacion().equals(codigoRecuperacion)) {
            throw new Exception("No hemos podido enviar el correo electrónico. Intentalo más tarde.");
        }

        // usamos el servicio de mail para mander el correo electronico de recuperacion
        mailService.enviarCorreoEnSegundoPlano(actualizacion.getEmail(),
                actualizacion.getCodigoRecuperacion(), 2);
        return "Te hemos enviado un correo electrónico con las "
                + "instrucciones para recuperar tu cuenta. Por favor revisa tu bandeja de entrada.";
    }

    public LoginDto iniciarSesion(Usuario log) throws Exception {
        try {
            // validamos la password
            this.validarAtributo(log, "email");
            // validamos la password
            this.validarAtributo(log, "password");
            Optional<Usuario> busquedaUsuario = usuarioRepository.findByEmail(log.getEmail());

            if (busquedaUsuario.isEmpty()) {
                throw new Exception("Correo electronico incorrecto.");
            }

            Usuario usuario = busquedaUsuario.get();

            // si la fecha de eliminacion no es nula entonces ya ha sido eliminado ese
            // usuario
            if (usuario.getDeletedAt() != null) {
                throw new Exception("Usuario ya ha sido eliminado.");
            }

            authenticationManager.authenticate(
                    // autenticar el usuario con la contrasenia encriptada
                    new UsernamePasswordAuthenticationToken(log.getEmail(),
                            log.getPassword()));

            // cargamos el usuario por el nombre
            UserDetails userDetails = authenticationService.loadUserByUsername(
                    log.getEmail());
            // generar el token
            String jwt = jwtGenerator.generateToken(userDetails);

            // Si el usuario tiene habilitado el 2FA, entonces se envía el código de
            // autenticación
            if (usuario.isTwoFactorEnabled()) {
                // creamos el codigo de recuperacion
                String codigoRecuperacion = generarCodigoRecuperacion();
                String codigoRecuperacionEncriptado = Encriptador.encriptarPassword(codigoRecuperacion);
                // actualizamos el codigo de recuperacion
                usuario.setTwoFactorCode(codigoRecuperacionEncriptado);
                // actualizamos en la bd
                Usuario actualizacion = usuarioRepository.save(usuario);
                mailService.enviarCorreoEnSegundoPlano(actualizacion.getEmail(), codigoRecuperacion, 1);
            }
            return new LoginDto(usuario, (usuario.isTwoFactorEnabled() ? null : jwt), usuario.isTwoFactorEnabled());
        } catch (AuthenticationException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public String generarCodigoRecuperacion() {
        Random random = new Random();
        int codigoRecuperacion = 10000 + random.nextInt(90000);
        return String.valueOf(codigoRecuperacion);
    }

    public LoginDto login2FT(Usuario log) throws Exception {
        try {
            // validamos la password
            this.validarAtributo(log, "email");
            // validamos la password
            this.validarAtributo(log, "twoFactorCode");
            Optional<Usuario> busquedaUsuario = usuarioRepository.findByEmail(log.getEmail());

            if (busquedaUsuario.isEmpty()) {
                throw new Exception("Correo electronico incorrecto.");
            }

            Usuario usuario = busquedaUsuario.get();

            // si la fecha de eliminacion no es nula entonces ya ha sido eliminado ese
            // usuario
            if (usuario.getDeletedAt() != null) {
                throw new Exception("Usuario ya ha sido eliminado.");
            }

            if (usuario.isTwoFactorEnabled()) {
                if (usuario.getTwoFactorCode().isEmpty()) {
                    throw new Exception("Medio de autenticación no disponible.");
                }
            }

            // Verificamos que el cliente tenga el mismo código de autenticación
            if (!Encriptador.compararPassword(log.getTwoFactorCode(), usuario.getTwoFactorCode())) {
                throw new Exception("Código de autenticación incorrecto.");
            }

            // Autenticación manual del usuario (sin contraseña)
            // Crear un token de autenticación preautenticado
            Authentication auth = new UsernamePasswordAuthenticationToken(log.getEmail(), null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Cargar los detalles del usuario por nombre de usuario
            UserDetails userDetails = authenticationService.loadUserByUsername(log.getEmail());

            // Generar el token JWT
            String jwt = jwtGenerator.generateToken(userDetails);

            // Devolver la respuesta con el usuario y el token
            return new LoginDto(usuario, jwt);

        } catch (AuthenticationException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Transactional
    public String recuperarPassword(PasswordChange cambioPassword) throws Exception {
        // validamos
        this.validar(cambioPassword);

        // para cambiar la password necesitamos obtener el usuario que solicito mediante
        // el codigo
        Optional<Usuario> busqueda = this.usuarioRepository
                .findByCodigoRecuperacion(cambioPassword.getCodigo());

        if (busqueda.isEmpty()) {// si esta vacio entonces el codigo no existe y devolvemos false
            throw new Exception("Tu código de autorización invalido.");
        }

        Usuario usuarioEncontrado = busqueda.get();

        // vemos si el usuario no ha sido eliminado
        if (usuarioEncontrado.getDeletedAt() != null) {
            throw new Exception("Usuario ya ha sido eliminado.");
        }

        // mandamos a borrar el codigo de recuperacion
        usuarioEncontrado.setCodigoRecuperacion(null);
        // encriptamos la password y hacemos el cambio en el modelo
        usuarioEncontrado.setPassword(
                Encriptador.encriptarPassword(
                        cambioPassword.getNuevaPassword()));

        Usuario update = this.usuarioRepository.save(usuarioEncontrado);

        if (update == null || update.getId().longValue() != usuarioEncontrado.getId().longValue()) {
            throw new Exception("No pudimos actualizar tu contraseña, inténtalo más tarde.");
        }

        return "Se cambió tu contraseña con exito.";
    }

    @Transactional
    public String cambiarPassword(Usuario usuPassChange, String emailUsuarioAutenticado) throws Exception {
        // que el id no este vacio
        if (usuPassChange.getId() == null || usuPassChange.getId() <= 0) {
            throw new Exception("Id inválido.");
        }
        // validamos la password
        this.validarAtributo(usuPassChange, "password");
        // buscamos el usuario
        Optional<Usuario> busquedaUsuario = usuarioRepository.findById(usuPassChange.getId());

        if (busquedaUsuario.isEmpty()) {
            throw new Exception("No hemos encontrado el usuario.");
        }

        Usuario usuarioEncontrado = busquedaUsuario.get();

        // vemos si el usuario no ha sido eliminado
        if (usuarioEncontrado.getDeletedAt() != null) {
            throw new Exception("Usuario ya ha sido eliminado.");
        }

        // validar si el usuario tiene permiso de eliminar
        this.verificarUsuarioJwt(usuarioEncontrado, emailUsuarioAutenticado);

        // encriptamos la password y hacemos el cambio en el modelo
        usuarioEncontrado.setPassword(
                Encriptador.encriptarPassword(usuPassChange.getPassword()));

        Usuario update = this.usuarioRepository.save(usuarioEncontrado);

        // mandamos a editar la password y comparamos si se hizo el cambio
        if (update.getId().longValue() == usuarioEncontrado.getId().longValue()) {
            return "Se cambió tu contraseña con exito.";
        }
        throw new Exception("No pudimos actualizar tu contraseña, inténtalo más tarde.");
    }

    /*
    @Transactional
    public Usuario crearAyudante(UsuarioAyudanteRequest crear) throws Exception {
        // validamos
        this.validar(crear.getUsuario());
        // traer rol AYUDANTE
        Rol rol = this.rolService.getRol("AYUDANTE");
        Usuario usuario = this.guardarUsuario(crear.getUsuario(), rol);
        // mandamos a guardar todos los permisos para el usuario
        Usuario actualizarPermisosUsuario = this.actualizarPermisosUsuario(
                new UsuarioPermisoRequest(usuario.getId(), crear.getPermisos()));
        return actualizarPermisosUsuario;
    }
    */

    @Transactional
    public Usuario crearAdministrador(Usuario crear) throws Exception {
        // validamos
        this.validar(crear);
        // traer rol AYUDANTE
        Rol rol = this.rolService.getRol("ADMIN");
        return this.guardarUsuario(crear, rol);
    }

    /**
     *
     * @param crear
     * @param rol
     * @return
     * @throws Exception
     */
    @Transactional
    public LoginDto crearUsuarioNormal(Usuario crear) throws Exception {
        // validamos
        this.validar(crear);
        // traer rol AYUDANTE
        Rol rol = this.rolService.getRol("USUARIO");
        // guardamos el usuario
        Usuario userCreado = this.guardarUsuario(crear, rol);
        // Generar el JWT para el usuario creado
        UserDetails userDetails = authenticationService.loadUserByUsername(crear.getEmail());
        String jwt = jwtGenerator.generateToken(userDetails);
        // Retornar la confirmación con el JWT
        if (userCreado.getId() > 0) {
            return new LoginDto(userCreado, jwt);
        }
        throw new Exception("No pudimos crear tu usuario, inténtalo más tarde.");
    }

    @Transactional
    private Usuario guardarUsuario(Usuario crear, Rol rol) throws Exception {
        if (this.usuarioRepository.existsByEmail(crear.getEmail())) {
            throw new Exception("El Email ya existe.");
        }
        // Asignamos un rol al usuario
        UsuarioRol usuarioRol = new UsuarioRol(crear, rol);
        ArrayList<UsuarioRol> rols = new ArrayList<>();
        rols.add(usuarioRol);
        crear.setRoles(rols);

        // Encriptar la contraseña
        crear.setPassword(this.encriptador.encriptarPassword(crear.getPassword()));

        // Guardar el usuario
        return this.usuarioRepository.save(crear);
    }

    /**
     * Agregar un permiso a un usuario
     *
     * @param usuarioPermiso
     * @return
     */
    @Transactional
    public Usuario actualizarPermisosUsuario(UsuarioPermisoRequest usuarioPermiso) throws Exception {
        // Buscamos el usuario en la base de datos
        Usuario usuario = this.getUsuario(usuarioPermiso.getIdUsuario());

        List<UsuarioPermiso> permisosNuevos = new ArrayList<>();
        // por cada permiso que se haya especificado creamos un nuevo permiso
        for (Permiso item : usuarioPermiso.getPermisos()) // Creamos el permiso
        {
            permisosNuevos.add(new UsuarioPermiso(
                    usuario, item));
        }

        if (usuario.getPermisos() == null) {
            usuario.setPermisos(permisosNuevos);
        } else {
            // asignamos los nuevos permisos al usuario
            usuario.getPermisos().clear();
            usuario.getPermisos().addAll(permisosNuevos);
        }
        // Guardamos el usuario
        return this.usuarioRepository.save(usuario);
    }

    /**
     * Agregar un permiso a un usuario
     *
     * @param usuario
     * @param permiso
     * @return
     */
    @Transactional
    public Usuario agregarPermisoUsuario(Usuario usuario, Permiso permiso) throws Exception {
        if (!this.usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El usuario no existe.");
        }
        // Buscamos el usuario en la base de datos
        Optional<Usuario> busquedaUsuario = usuarioRepository.findById(usuario.getId());
        if (busquedaUsuario.isEmpty()) {
            throw new Exception("No hemos encontrado el usuario.");
        }
        // Verificamos que el usuario no haya sido eliminado
        if (busquedaUsuario.get().getDeletedAt() != null) {
            throw new Exception("Usuario ya ha sido eliminado.");
        }
        Usuario usuarioEncontrado = busquedaUsuario.get();
        // Creamos el permiso
        UsuarioPermiso usuarioPermiso = new UsuarioPermiso(usuarioEncontrado, permiso);
        usuario.keepOrphanRemoval(usuarioEncontrado);
        if (usuario.getPermisos() == null) {
            usuario.setPermisos(new ArrayList<>());
        }
        // Verificamos si el permiso ya existe
        if (usuario.getPermisos().stream().anyMatch(p -> p.getPermiso().getId().equals(permiso.getId()))) {
            throw new IllegalArgumentException("El permiso ya ha sido asignado al usuario.");
        }
        // Agregamos el permiso a la lista de permisos del usuario
        usuario.getPermisos().add(usuarioPermiso);
        // Guardamos el usuario
        return this.usuarioRepository.save(usuario);
    }

    private boolean verificarUsuarioJwt(Usuario usuarioTratar, String emailUsuarioAutenticado) throws Exception {
        // validar si el usuario tiene permiso de eliminar
        if (!emailUsuarioAutenticado.equals(usuarioTratar.getEmail())
                && !isUserAdmin(emailUsuarioAutenticado)) {
            throw new Exception("No tienes permiso para realizar acciones a este usuario.");
        }
        return true;
    }

    /**
     * Obtiene todos los usuarios por su rol
     *
     * @param crear
     * @return
     * @throws Exception
     */
    public List<Usuario> getUsuariosByRol(Rol rol) throws Exception {
        // validamos
        this.validarAtributo(rol, "nombre");
        // traer rol AYUDANTE
        Rol rolSearch = this.rolService.getRol(rol.getNombre());
        // buscar todos los usuarios por rol
        return this.ignorarEliminados(this.usuarioRolRepository.findUsuariosByRolNombre(
                rolSearch.getNombre()));
    }
}
