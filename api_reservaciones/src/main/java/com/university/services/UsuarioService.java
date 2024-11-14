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

import com.university.models.HorarioAtencionUsuario;
import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Usuario;
import com.university.models.PermisoRol;
import com.university.models.UsuarioRol;
import com.university.models.dto.LoginDto;
import com.university.models.request.PasswordChange;
import com.university.models.request.RolesUsuarioRequest;
import com.university.models.request.TwoFactorActivate;
import com.university.models.request.CreateUsuarioDto;
import com.university.models.request.HorariosUsuarioRequest;
import com.university.repositories.HorarioAtencionUsuarioRepository;
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
    @Autowired
    private HorarioAtencionUsuarioRepository horarioAtencionUsuarioRepository;

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
        if (id == null || id <= 0) {
            throw new Exception("Id invalido.");
        }

        Optional<Usuario> busquedaUsuario = usuarioRepository.findById(id);

        if (busquedaUsuario.isEmpty()) {
            throw new Exception("No hemos encontrado el usuario.");
        }

        Usuario usuarioEncontrado = busquedaUsuario.get();

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
        // usuario.setFacturas(usuarioEncontrado.getFacturas());
        usuario.setRoles(usuarioEncontrado.getRoles());
        usuario.setHorariosAtencionUsuario(usuarioEncontrado.getHorariosAtencionUsuario());
        usuario.setVerificado(usuarioEncontrado.isVerificado());
        // usuario.setPermisos(usuarioEncontrado.getPermisos());
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

        if (actualizacion.getCodigoRecuperacion() == null
                || !actualizacion.getCodigoRecuperacion().equals(codigoRecuperacion)) {
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
            return new LoginDto(usuario, (usuario.isTwoFactorEnabled() ? null : jwt), usuario.isTwoFactorEnabled(), usuario.getVerificado());
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
            return new LoginDto(usuario, jwt, usuario.isTwoFactorEnabled(), usuario.getVerificado());

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
    public String verificarUsuario(String codigoVerificacion) throws Exception {
        Optional<Usuario> busqueda = this.usuarioRepository
                .findByCodigoVerificacion(codigoVerificacion);

        if (busqueda.isEmpty()) {// si esta vacio entonces el codigo no existe y devolvemos false
            throw new Exception("Tu código de autorización invalido.");
        }

        Usuario usuarioEncontrado = busqueda.get();

        // vemos si el usuario no ha sido eliminado
        if (usuarioEncontrado.getDeletedAt() != null) {
            throw new Exception("Usuario ya ha sido eliminado.");
        }

        usuarioEncontrado.setCodigoVerificacion(null);
        usuarioEncontrado.setVerificado(true);

        Usuario update = this.usuarioRepository.save(usuarioEncontrado);

        if (update.getId().longValue() != usuarioEncontrado.getId().longValue()) {
            throw new Exception("No pudimos verificar tu usuario, inténtalo más tarde.");
        }

        return "Se verifico tu usuario con exito.";
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
     * @Transactional
     * public Usuario crearAyudante(UsuarioAyudanteRequest crear) throws Exception {
     * // validamos
     * this.validar(crear.getUsuario());
     * // traer rol AYUDANTE
     * Rol rol = this.rolService.getRol("AYUDANTE");
     * Usuario usuario = this.guardarUsuario(crear.getUsuario(), rol);
     * // mandamos a guardar todos los permisos para el usuario
     * Usuario actualizarPermisosUsuario = this.actualizarPermisosUsuario(
     * new UsuarioPermisoRequest(usuario.getId(), crear.getPermisos()));
     * return actualizarPermisosUsuario;
     * }
     */

     @Transactional(rollbackOn = Exception.class)
    public String cambiarTwoFactor(TwoFactorActivate cambio, String usuarioString) throws Exception {
        this.validar(cambio);// validar objeto
        Usuario usuario = this.getUsuario(cambio.getId());
        this.verificarUsuarioJwt(usuario, usuarioString);
        if (usuario.isTwoFactorEnabled() && cambio.getActivacion()) {
            throw new Exception("Esto ya esta activado.");
        }

        if (!usuario.isTwoFactorEnabled() && !cambio.getActivacion()) {
            throw new Exception("Esto ya esta desactivado.");
        }

        usuario.setTwoFactorEnabled(cambio.getActivacion());
        Usuario save = this.usuarioRepository.save(usuario);
        if (save.getId() > 0) {
            if (save.isTwoFactorEnabled()) {
                return "Se activo el two factor con exito";
            } else {
                return "Se desactivo el two factor con exito";
            }
        }
        throw new Exception("Error al tratar de actualizar el two factor.");
    }

    @Transactional
    public Usuario crearAdministrador(Usuario crear) throws Exception {
        Rol rol = this.rolService.getRol("ADMIN");
        List<Rol> rolAdmin = new ArrayList<>();
        rolAdmin.add(rol);
        return this.guardarUsuario(crear, rolAdmin);
    }

    @Transactional
    public Usuario crearAyudante(CreateUsuarioDto crear) throws Exception {
        Usuario usuario = crear.getUsuario();
        usuario.setVerificado(true);
        List<Rol> roles = crear.getRoles();
        List<HorarioAtencionUsuario> horarios = crear.getHorarioAtencionUsuarios();
        return this.guardarUsuario(usuario, roles, horarios);
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
        Rol rol = this.rolService.getRol("USUARIO");
        // guardamos el usuario
        List<Rol> rolAdmin = new ArrayList<>();
        rolAdmin.add(rol);
        Usuario userCreado = this.guardarUsuario(crear, rolAdmin);

        // creamos el codigo de verificacion
        String codigoVerificacion = UUID.randomUUID().toString();
        // actualizamos el codigo de recuperacion
        userCreado.setCodigoVerificacion(codigoVerificacion);
        // actualizamos en la bd
        Usuario actualizacion = usuarioRepository.save(userCreado);

        if (actualizacion.getCodigoVerificacion() == null
                || !actualizacion.getCodigoVerificacion().equals(codigoVerificacion)) {
            throw new Exception("No hemos podido enviar el correo electrónico. Intentalo más tarde.");
        }

        // usamos el servicio de mail para mander el correo electronico de recuperacion
        mailService.enviarCorreoEnSegundoPlano(actualizacion.getEmail(),
                actualizacion.getCodigoVerificacion(), 3);

        // Retornar la confirmación con el JWT
        // Generar el JWT para el usuario creado
        UserDetails userDetails = authenticationService.loadUserByUsername(crear.getEmail());
        String jwt = jwtGenerator.generateToken(userDetails);
        if (userCreado.getId() > 0) {
            return new LoginDto(userCreado, jwt);
        }
        throw new Exception("No pudimos crear tu usuario, inténtalo más tarde.");
    }

    @Transactional
    private Usuario guardarUsuario(Usuario crear, List<Rol> roles) throws Exception {
        if (this.usuarioRepository.existsByEmail(crear.getEmail())) {
            throw new Exception("El Email ya existe.");
        }

        ArrayList<UsuarioRol> rols = new ArrayList<>();
        for (Rol rolCreado : roles) {
            // Asignamos un rol al usuario
            UsuarioRol usuarioRol = new UsuarioRol(crear, rolCreado);
            rols.add(usuarioRol);
        }
        crear.setRoles(rols);

        // Encriptar la contraseña
        crear.setPassword(this.encriptador.encriptarPassword(crear.getPassword()));

        // Guardar el usuario
        return this.usuarioRepository.save(crear);
    }

    @Transactional
    private Usuario guardarUsuario(Usuario crear, List<Rol> roles, List<HorarioAtencionUsuario> horarios) throws Exception {
        if (this.usuarioRepository.existsByEmail(crear.getEmail())) {
            throw new Exception("El Email ya existe.");
        }

        ArrayList<UsuarioRol> rols = new ArrayList<>();
        for (Rol rolCreado : roles) {
            // Asignamos un rol al usuario
            UsuarioRol usuarioRol = new UsuarioRol(crear, rolCreado);
            rols.add(usuarioRol);
        }
        crear.setRoles(rols);
        Usuario crearRol = this.usuarioRepository.save(crear);

        List<HorarioAtencionUsuario> horariosAtencionCreados = new ArrayList<>();
        // Se crean los horarios de atencion que tendra el usuario
        for (HorarioAtencionUsuario horario : horarios) {
            HorarioAtencionUsuario horarioUsuarioCreado = new HorarioAtencionUsuario(
                    horario.getHoraInicio(), horario.getHoraFinal(),
                    horario.getDiaAtencion());
            horarioUsuarioCreado.setUsuario(crearRol);

            HorarioAtencionUsuario horarioCreadoActual = horarioAtencionUsuarioRepository.save(horarioUsuarioCreado);
            //horarioCreadoActual.setUsuario(crearRol);
            horarioAtencionUsuarioRepository.save(horarioCreadoActual);

            horariosAtencionCreados.add(horarioCreadoActual);
        }

        crearRol.setHorariosAtencionUsuario(horariosAtencionCreados);

        // Encriptar la contraseña
        crearRol.setPassword(this.encriptador.encriptarPassword(crearRol.getPassword()));

        // Guardar el usuario
        return this.usuarioRepository.save(crearRol);
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

        Rol rolSearch = this.rolService.getRol(rol.getNombre());
        // buscar todos los usuarios por rol
        return this.ignorarEliminados(this.usuarioRolRepository.findUsuariosByRolNombre(
                rolSearch.getNombre()));
    }

    /**
     * Sobreescribir los horarios de un usuario
     *
     * @param HorariosUsuarioRequest
     * @return
     */
    @Transactional
    public Usuario actualizarHorariosUsuario(HorariosUsuarioRequest horariosAtencionUsuario) throws Exception {
        // Buscamos el usuario en la base de datos
        Usuario usuario = this.getByEmail(horariosAtencionUsuario.getEmailUsuario());

        List<HorarioAtencionUsuario> horariosNuevos = new ArrayList<>();
        // por cada permiso que se haya especificado creamos un nuevo permiso
        for (HorarioAtencionUsuario horarioAtencionUsuario : horariosAtencionUsuario.getHorariosAtencionUsuario()) {
            horariosNuevos.add(new HorarioAtencionUsuario(horarioAtencionUsuario.getHoraInicio(),
                    horarioAtencionUsuario.getHoraFinal(), horarioAtencionUsuario.getDiaAtencion(), usuario));
        }

        if (usuario.getHorariosAtencionUsuario() == null) {
            usuario.setHorariosAtencionUsuario(horariosNuevos);
        } else {
            // asignamos los nuevos permisos al usuario
            usuario.getHorariosAtencionUsuario().clear();
            usuario.getHorariosAtencionUsuario().addAll(horariosNuevos);
        }
        // Guardamos el usuario
        return this.usuarioRepository.save(usuario);
    }

    /**
     * Sobreescribir los roles de un usuario
     *
     * @param RolesUsuarioRequest
     * @return
     */
    @Transactional
    public Usuario actualizarRolesUsuario(RolesUsuarioRequest rolesUsuario) throws Exception {
        // Buscamos el usuario en la base de datos
        Usuario usuario = this.getByEmail(rolesUsuario.getEmailUsuario());

        List<UsuarioRol> rolesNuevos = new ArrayList<>();
        // por cada permiso que se haya especificado creamos un nuevo permiso
        for (Rol rolActual : rolesUsuario.getRolesUsuario()) {
            rolesNuevos.add(new UsuarioRol(usuario, rolActual));
        }

        if (usuario.getHorariosAtencionUsuario() == null) {
            usuario.setRoles(rolesNuevos);
        } else {
            // asignamos los nuevos permisos al usuario
            usuario.getRoles().clear();
            usuario.getRoles().addAll(rolesNuevos);
        }
        // Guardamos el usuario
        return this.usuarioRepository.save(usuario);
    }
}
