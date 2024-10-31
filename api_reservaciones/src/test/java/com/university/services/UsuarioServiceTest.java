package com.university.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.university.models.DiaAtencion;
import com.university.models.HorarioAtencionUsuario;
import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Usuario;
import com.university.models.PermisoRol;
import com.university.models.UsuarioRol;
import com.university.models.dto.LoginDto;
import com.university.models.request.PasswordChange;
import com.university.models.request.TwoFactorActivate;
import com.university.models.request.HorariosUsuarioRequest;
import com.university.repositories.UsuarioRepository;
import com.university.repositories.UsuarioRolRepository;
import com.university.services.authentication.AuthenticationService;
import com.university.services.authentication.JwtGeneratorService;
import com.university.tools.Encriptador;
import com.university.tools.MailService;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Validator validator;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtGeneratorService jwtGenerator;

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private RolService rolService;

    @Mock
    private MailService mailService;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializar mocks
    }

    @Test
    void testCambiarPassword() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@correo.com");
        usuario.setPassword("newPassword");

        //when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));

         // Simular la validación del password sin errores
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(validator.validateProperty(any(), eq("password"))).thenReturn(Collections.emptySet());

        when(usuarioRepository.save(usuario)).thenAnswer(invocation -> {
            Usuario savedUser = invocation.getArgument(0);
            return savedUser;
        });

        usuarioRepository.save(usuario);

        String result;
        try {
            result = usuarioService.cambiarPassword(usuario, "test@correo.com");
            assertEquals("Se cambió tu contraseña con exito." , result);
        } catch (Exception e) {
            fail(e);
        }

        //verify(usuarioRepository, times(1)).findByEmail("test@correo.com");
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(2)).save(usuario);
    }

    @Test
    void testIniciarSesion() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@correo.com");
        usuario.setPassword("password");

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setRol(new Rol("ADMIN"));  // Simulamos un rol
        ArrayList<UsuarioRol> roles = new ArrayList<>();
        roles.add(usuarioRol);
        usuario.setRoles(roles);  // Asignamos roles al usuario

        when(usuarioRepository.findByEmail("test@correo.com")).thenReturn(Optional.of(usuario));

         UserDetails userDetails = User.withUsername("test@example.com")
                                      .password("password")
                                      .roles("ADMIN")
                                      .build();

        when(authenticationService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        when(jwtGenerator.generateToken(userDetails)).thenReturn("dummy-jwt-token");

        // Simular el proceso de autenticación
        LoginDto respuesta;
        try {
            respuesta = usuarioService.iniciarSesion(usuario);
            assertNotNull(respuesta);
            assertEquals(usuario.getEmail(), respuesta.getUsuario().getEmail());
        } catch (Exception e) {
            fail(e);
        }


        verify(usuarioRepository, times(1)).findByEmail("test@correo.com");
    }

    @Test
    void testGetByEmail_Success() throws Exception {
        // Simular un usuario válido
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setDeletedAt(null);  // El usuario no ha sido eliminado

        // Simular que se encuentra el usuario por su email
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        // Llamar al método y verificar que retorna el usuario correcto
        Usuario resultado = usuarioService.getByEmail("test@example.com");
        assertNotNull(resultado);
        assertEquals("test@example.com", resultado.getEmail());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetByEmail_UserNotFound() {
        // Simular que no se encuentra el usuario por su email
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.getByEmail("test@example.com");
        });

        assertEquals("Usuario no encontrado.", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetByEmail_UserDeleted() {
        // Simular un usuario que ha sido eliminado (deletedAt no es null)
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setDeletedAt(Instant.now());  // Simulamos que fue eliminado

        // Simular que se encuentra el usuario por su email
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.getByEmail("test@example.com");
        });

        assertEquals("Usuario eliminado.", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetUsuario_Success() throws Exception {
        // Simular un usuario válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setDeletedAt(null);  // El usuario no ha sido eliminado

        // Simular que se encuentra el usuario por su id
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Llamar al método y verificar que retorna el usuario correcto
        Usuario resultado = usuarioService.getUsuario(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUsuario_InvalidId_Null() {
        // Llamar al método con un id nulo y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.getUsuario(null);
        });

        assertEquals("Id invalido.", exception.getMessage());
    }

    @Test
    void testGetUsuario_InvalidId_Negative() {
        // Llamar al método con un id inválido (negativo o cero) y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.getUsuario(0L);
        });

        assertEquals("Id invalido.", exception.getMessage());
    }

    @Test
    void testGetUsuario_UserNotFound() {
        // Simular que no se encuentra el usuario por su id
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.getUsuario(1L);
        });

        assertEquals("No hemos encontrado el usuario.", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUsuario_UserDeleted() {
        // Simular un usuario que ha sido eliminado (deletedAt no es null)
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setDeletedAt(Instant.now());  // Simulamos que fue eliminado

        // Simular que se encuentra el usuario por su id
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.getUsuario(1L);
        });

        assertEquals("Usuario ya ha sido eliminado.", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findById(1L);
    }
    @Test
    void testEliminarUsuario_InvalidId() {
        // Llamar al método con un id inválido y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.eliminarUsuario(null, "authUser@example.com");
        });

        assertEquals("Id invalido.", exception.getMessage());
    }

    @Test
    void testEliminarUsuario_UserNotFound() {
        // Simular que no se encuentra el usuario por su id
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.eliminarUsuario(1L, "authUser@example.com");
        });

        assertEquals("No hemos encontrado el usuario.", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testEliminarUsuario_Success() throws Exception {
        // Simular un usuario válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");

        // Simular que se encuentra el usuario por su id
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Simular que el usuario tiene permiso para eliminar
        UserDetails userDetails = User.withUsername("authUser@example.com")
                                      .password("password")
                                      .roles("ADMIN")  // Simulamos que tiene el rol adecuado
                                      .build();
        when(authenticationService.loadUserByUsername("authUser@example.com")).thenReturn(userDetails);

        // Simular que la eliminación del usuario fue exitosa
        when(usuarioRepository.deleteUsuarioById(1L)).thenReturn(1L);

        // Llamar al método y verificar que retorna el mensaje correcto
        String resultado = usuarioService.eliminarUsuario(1L, "authUser@example.com");
        assertEquals("Se elimino el usuario con exito.", resultado);

        // Verificar que los métodos del repositorio fueron llamados correctamente
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).deleteUsuarioById(1L);
    }

    @Test
    void testEliminarUsuario_FailedDeletion() throws Exception {
        // Simular un usuario válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");

        // Simular que se encuentra el usuario por su id
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Simular que el usuario tiene permiso para eliminar
        UserDetails userDetails = User.withUsername("authUser@example.com")
                                      .password("password")
                                      .roles("ADMIN")  // Simulamos que tiene el rol adecuado
                                      .build();
        when(authenticationService.loadUserByUsername("authUser@example.com")).thenReturn(userDetails);

        // Simular que la eliminación falla (deleteUsuarioById retorna 0)
        when(usuarioRepository.deleteUsuarioById(1L)).thenReturn(0L);

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.eliminarUsuario(1L, "authUser@example.com");
        });

        assertEquals("No pudimos eliminar el usuario, inténtalo más tarde.", exception.getMessage());

        // Verificar que los métodos del repositorio fueron llamados correctamente
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).deleteUsuarioById(1L);
    }
    private void simulateAuthenticatedUser(String email) {
        // Simulamos un usuario autenticado
        UserDetails userDetails = User.withUsername(email)
                                      .password("password")
                                      .roles("ADMIN")  // Simulamos que tiene el rol adecuado
                                      .build();
        when(authenticationService.loadUserByUsername(email)).thenReturn(userDetails);
    }

    @Test
    void testUpdateUsuario_InvalidId() {
        // Simular un usuario con un ID inválido
        Usuario usuario = new Usuario();
        usuario.setId(null);  // ID nulo

        // Verificar que se lanza la excepción por ID inválido
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.updateUsuario(usuario, "authUser@example.com");
        });

        assertEquals("Id inválido.", exception.getMessage());
    }

    @Test
    void testUpdateUsuario_UserNotFound() {
        // Simular un usuario con un ID válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        // Simular que el usuario no es encontrado en la base de datos
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción por usuario no encontrado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.updateUsuario(usuario, "authUser@example.com");
        });

        assertEquals("No hemos encontrado el usuario.", exception.getMessage());

        // Verificar que el repositorio fue llamado correctamente
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUsuario_UserDeleted() {
        // Simular un usuario con un ID válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        // Simular un usuario que ha sido eliminado (tiene `deletedAt`)
        Usuario usuarioEncontrado = new Usuario();
        usuarioEncontrado.setId(1L);
        usuarioEncontrado.setDeletedAt(Instant.now());

        // Simular que se encuentra el usuario pero ha sido eliminado
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEncontrado));

        // Simular la autenticación del usuario
        simulateAuthenticatedUser("authUser@example.com");

        // Verificar que se lanza la excepción por usuario eliminado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.updateUsuario(usuario, "authUser@example.com");
        });

        assertEquals("Usuario ya ha sido eliminado.", exception.getMessage());
    }

    @Test
    void testUpdateUsuario_EmailAlreadyExists() {
        // Simular un usuario que será actualizado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");

        // Simular un usuario encontrado en la base de datos
        Usuario usuarioEncontrado = new Usuario();
        usuarioEncontrado.setId(1L);
        usuarioEncontrado.setEmail("user@example.com");

        // Simular que el usuario existe en la base de datos
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEncontrado));

        // Simular que existe otro usuario con el mismo email
        when(usuarioRepository.existsUsuarioByEmailAndIdNot("user@example.com", 1L)).thenReturn(true);

        // Simular la autenticación del usuario
        simulateAuthenticatedUser("authUser@example.com");

        // Verificar que se lanza la excepción por email duplicado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.updateUsuario(usuario, "authUser@example.com");
        });

        assertEquals("No se editó el usuario user@example.com, debido a que ya existe otro usuario con el mismo email.", exception.getMessage());
    }

    @Test
    void testUpdateUsuario_Success() throws Exception {
        // Simular un usuario que será actualizado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");

        // Simular un usuario encontrado en la base de datos
        Usuario usuarioEncontrado = new Usuario();
        usuarioEncontrado.setId(1L);
        usuarioEncontrado.setEmail("user@example.com");
        usuarioEncontrado.setPassword("password");
        usuarioEncontrado.setDeletedAt(null);

        // Simular que se encuentra el usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEncontrado));

        // Simular que no hay otro usuario con el mismo email
        when(usuarioRepository.existsUsuarioByEmailAndIdNot("user@example.com", 1L)).thenReturn(false);

        // Simular la actualización exitosa del usuario
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Simular la autenticación del usuario
        simulateAuthenticatedUser("authUser@example.com");

        // Llamar al método y verificar que retorna el usuario actualizado
        Usuario resultado = usuarioService.updateUsuario(usuario, "authUser@example.com");
        assertNotNull(resultado);
        assertEquals("user@example.com", resultado.getEmail());

        // Verificar que los métodos del repositorio fueron llamados correctamente
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testUpdateUsuario_FailedUpdate() throws Exception {
        // Simular un usuario que será actualizado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");

        // Simular un usuario encontrado en la base de datos
        Usuario usuarioEncontrado = new Usuario();
        usuarioEncontrado.setId(1L);
        usuarioEncontrado.setEmail("user@example.com");
        usuarioEncontrado.setPassword("password");

        // Simular que se encuentra el usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEncontrado));

        // Simular que no hay otro usuario con el mismo email
        when(usuarioRepository.existsUsuarioByEmailAndIdNot("user@example.com", 1L)).thenReturn(false);

        // Simular que la actualización falla (el `save` retorna null)
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(null);

        // Simular la autenticación del usuario
        simulateAuthenticatedUser("authUser@example.com");

        // Verificar que se lanza la excepción por fallo en la actualización
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.updateUsuario(usuario, "authUser@example.com");
        });

        assertEquals("No pudimos actualizar el usuario, inténtalo más tarde.", exception.getMessage());
    }

    @Test
    void testLogin2FT_Success() throws Exception {
        // Simular el objeto de log (usuario que intenta iniciar sesión)
        Usuario log = new Usuario();
        log.setEmail("user@example.com");
        log.setTwoFactorCode("123456");

        // Simular un usuario encontrado en la base de datos
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setTwoFactorCode("123456");  // Simular que el código está encriptado
        usuario.setTwoFactorEnabled(true);

        // Simular que el usuario es encontrado
        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));

        // Simular el método estático Encriptador.compararPassword
        try (MockedStatic<Encriptador> encriptadorMockedStatic = mockStatic(Encriptador.class)) {
            encriptadorMockedStatic.when(() -> Encriptador.compararPassword("123456", "123456")).thenReturn(true);

            // Simular los detalles del usuario autenticado
            UserDetails userDetails = mock(UserDetails.class);
            when(authenticationService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

            // Simular la generación del token JWT
            when(jwtGenerator.generateToken(userDetails)).thenReturn("dummy-jwt-token");

            // Llamar al método y verificar que devuelve el DTO con el token correcto
            LoginDto result = usuarioService.login2FT(log);
            assertNotNull(result);
            assertEquals("dummy-jwt-token", result.getJwt());

            // Verificar que los métodos necesarios fueron llamados
            verify(usuarioRepository, times(1)).findByEmail("user@example.com");
            verify(jwtGenerator, times(1)).generateToken(userDetails);
        }
    }

    @Test
    void testLogin2FT_UserNotFound() {
        // Simular el objeto de log
        Usuario log = new Usuario();
        log.setEmail("user@example.com");
        log.setTwoFactorCode("123456");

        // Simular que el usuario no es encontrado
        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.login2FT(log);
        });

        assertEquals("Correo electronico incorrecto.", exception.getMessage());
    }

    @Test
    void testLogin2FT_UserDeleted() {
        // Simular el objeto de log
        Usuario log = new Usuario();
        log.setEmail("user@example.com");
        log.setTwoFactorCode("123456");

        // Simular un usuario eliminado
        Usuario usuario = new Usuario();
        usuario.setDeletedAt(Instant.now());  // Usuario eliminado

        // Simular que el usuario es encontrado pero está eliminado
        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.login2FT(log);
        });

        assertEquals("Usuario ya ha sido eliminado.", exception.getMessage());
    }

    @Test
    void testLogin2FT_TwoFactorCodeMissing() {
        // Simular el objeto de log
        Usuario log = new Usuario();
        log.setEmail("user@example.com");
        log.setTwoFactorCode("123456");

        // Simular un usuario con autenticación de dos factores habilitada, pero sin código
        Usuario usuario = new Usuario();
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorCode("");  // Código de autenticación vacío

        // Simular que el usuario es encontrado
        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));

        // Llamar al método y verificar que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.login2FT(log);
        });

        assertEquals("Medio de autenticación no disponible.", exception.getMessage());
    }
    @Test
    void testLogin2FT_InvalidTwoFactorCode() {
        // Simular el objeto de log
        Usuario log = new Usuario();
        log.setEmail("user@example.com");
        log.setTwoFactorCode("123456");

        // Simular un usuario con autenticación de dos factores habilitada
        Usuario usuario = new Usuario();
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorCode("654321");  // Código incorrecto

        // Simular que el usuario es encontrado
        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));

        // Simular el método estático Encriptador.compararPassword
        try (MockedStatic<Encriptador> encriptadorMockedStatic = mockStatic(Encriptador.class)) {
            encriptadorMockedStatic.when(() -> Encriptador.compararPassword("123456", "654321")).thenReturn(false);

            // Llamar al método y verificar que lanza la excepción correcta
            Exception exception = assertThrows(Exception.class, () -> {
                usuarioService.login2FT(log);
            });

            assertEquals("Código de autenticación incorrecto.", exception.getMessage());
        }
    }

        @Test
    void testRecuperarPassword_Success() throws Exception {
        // Simular el objeto cambioPassword
        PasswordChange cambioPassword = new PasswordChange();
        cambioPassword.setCodigo("valid-code");
        cambioPassword.setNuevaPassword("new-password");

        // Simular un usuario válido encontrado por el código de recuperación
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigoRecuperacion("valid-code");

        // Simular que se encuentra el usuario por el código de recuperación
        when(usuarioRepository.findByCodigoRecuperacion("valid-code")).thenReturn(Optional.of(usuario));

        // Simular el método estático Encriptador.encriptarPassword
        try (MockedStatic<Encriptador> encriptadorMockedStatic = mockStatic(Encriptador.class)) {
            encriptadorMockedStatic.when(() -> Encriptador.encriptarPassword("new-password"))
                    .thenReturn("encrypted-password");

            // Simular el guardado del usuario actualizado
            usuario.setPassword("encrypted-password");
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            // Llamar al método y verificar el resultado
            String resultado = usuarioService.recuperarPassword(cambioPassword);
            assertEquals("Se cambió tu contraseña con exito.", resultado);

            // Verificar que los métodos fueron llamados
            verify(usuarioRepository, times(1)).findByCodigoRecuperacion("valid-code");
            verify(usuarioRepository, times(1)).save(usuario);
        }
    }

    @Test
    void testRecuperarPassword_InvalidCode() {
        // Simular el objeto cambioPassword
        PasswordChange cambioPassword = new PasswordChange();
        cambioPassword.setCodigo("invalid-code");
        cambioPassword.setNuevaPassword("new-password");

        // Simular que el usuario no es encontrado por el código de recuperación
        when(usuarioRepository.findByCodigoRecuperacion("invalid-code")).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.recuperarPassword(cambioPassword);
        });

        assertEquals("Tu código de autorización invalido.", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findByCodigoRecuperacion("invalid-code");
    }

    @Test
    void testRecuperarPassword_UserDeleted() {
        // Simular el objeto cambioPassword
        PasswordChange cambioPassword = new PasswordChange();
        cambioPassword.setCodigo("valid-code");
        cambioPassword.setNuevaPassword("new-password");

        // Simular un usuario eliminado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigoRecuperacion("valid-code");
        usuario.setDeletedAt(Instant.now());  // Usuario eliminado

        // Simular que el usuario es encontrado
        when(usuarioRepository.findByCodigoRecuperacion("valid-code")).thenReturn(Optional.of(usuario));

        // Verificar que se lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.recuperarPassword(cambioPassword);
        });

        assertEquals("Usuario ya ha sido eliminado.", exception.getMessage());

        // Verificar que el repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findByCodigoRecuperacion("valid-code");
    }

    @Test
    void testRecuperarPassword_FailedUpdate() throws Exception {
        // Simular el objeto cambioPassword
        PasswordChange cambioPassword = new PasswordChange();
        cambioPassword.setCodigo("valid-code");
        cambioPassword.setNuevaPassword("new-password");

        // Simular un usuario válido encontrado por el código de recuperación
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigoRecuperacion("valid-code");

        // Simular que se encuentra el usuario por el código de recuperación
        when(usuarioRepository.findByCodigoRecuperacion("valid-code")).thenReturn(Optional.of(usuario));

        // Simular el método estático Encriptador.encriptarPassword
        try (MockedStatic<Encriptador> encriptadorMockedStatic = mockStatic(Encriptador.class)) {
            encriptadorMockedStatic.when(() -> Encriptador.encriptarPassword("new-password"))
                    .thenReturn("encrypted-password");

            // Simular que la actualización de la contraseña falla (usuario guardado es nulo)
            when(usuarioRepository.save(usuario)).thenReturn(null);

            // Verificar que se lanza la excepción correcta
            Exception exception = assertThrows(Exception.class, () -> {
                usuarioService.recuperarPassword(cambioPassword);
            });

            assertEquals("No pudimos actualizar tu contraseña, inténtalo más tarde.", exception.getMessage());

            // Verificar que los métodos fueron llamados
            verify(usuarioRepository, times(1)).findByCodigoRecuperacion("valid-code");
            verify(usuarioRepository, times(1)).save(usuario);
        }
    }
    @Test
    void testCrearAdministrador_Success() throws Exception {
        // Simular el objeto Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@example.com");
        usuario.setPassword("password");

        // Simular el rol "ADMIN"
        Rol rol = new Rol();
        rol.setNombre("ADMIN");
        when(rolService.getRol("ADMIN")).thenReturn(rol);

        // Simular el método estático Encriptador.encriptarPassword
        try (MockedStatic<Encriptador> encriptadorMockedStatic = mockStatic(Encriptador.class)) {
            encriptadorMockedStatic.when(() -> Encriptador.encriptarPassword("password"))
                                   .thenReturn("encrypted-password");

            // Simular el guardado del usuario
            Usuario usuarioGuardado = new Usuario();
            usuarioGuardado.setId(1L);
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

            // Llamar al método y verificar que devuelve el usuario creado
            Usuario resultado = usuarioService.crearAdministrador(usuario);
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());

            // Verificar que los métodos necesarios fueron llamados
            verify(rolService, times(1)).getRol("ADMIN");
            verify(usuarioRepository, times(1)).save(usuario);
        }
    }

    @Test
    void testCrearAdministrador_EmailAlreadyExists() throws Exception {
        // Simular el objeto Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@example.com");

        // Simular que el email ya existe
        when(usuarioRepository.existsByEmail("admin@example.com")).thenReturn(true);

        // Verificar que se lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.crearAdministrador(usuario);
        });

        assertEquals("El Email ya existe.", exception.getMessage());
        verify(usuarioRepository, times(1)).existsByEmail("admin@example.com");
    }

    // Test para crearUsuarioNormal
    @Test
    void testCrearUsuarioNormal_Success() throws Exception {
        // Simular el objeto Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setPassword("password");

        // Simular el rol "USUARIO"
        Rol rol = new Rol();
        rol.setNombre("USUARIO");
        when(rolService.getRol("USUARIO")).thenReturn(rol);

        // Simular el método estático Encriptador.encriptarPassword
        try (MockedStatic<Encriptador> encriptadorMockedStatic = mockStatic(Encriptador.class)) {
            encriptadorMockedStatic.when(() -> Encriptador.encriptarPassword("password"))
                                   .thenReturn("encrypted-password");

            // Simular el guardado del usuario
            Usuario usuarioGuardado = new Usuario();
            usuarioGuardado.setId(1L);
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

            // Simular los detalles del usuario autenticado
            UserDetails userDetails = mock(UserDetails.class);
            when(authenticationService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

            // Simular la generación del token JWT
            when(jwtGenerator.generateToken(userDetails)).thenReturn("dummy-jwt-token");

            // Llamar al método y verificar que devuelve el LoginDto
            LoginDto resultado = usuarioService.crearUsuarioNormal(usuario);
            assertNotNull(resultado);
            assertEquals("dummy-jwt-token", resultado.getJwt());

            // Verificar que los métodos necesarios fueron llamados
            verify(rolService, times(1)).getRol("USUARIO");
            verify(usuarioRepository, times(1)).save(usuario);
            verify(jwtGenerator, times(1)).generateToken(userDetails);
        }
    }

    @Test
    void testCrearUsuarioNormal_EmailAlreadyExists() throws Exception {
        // Simular el objeto Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");

        // Simular que el email ya existe
        when(usuarioRepository.existsByEmail("user@example.com")).thenReturn(true);

        // Verificar que se lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.crearUsuarioNormal(usuario);
        });

        assertEquals("El Email ya existe.", exception.getMessage());
        verify(usuarioRepository, times(1)).existsByEmail("user@example.com");
    }

    @Test
    void testEnviarMailDeRecuperacion_EmailBlank() {
        // Simular el correo en blanco
        String correoVacio = "";

        // Verificar que se lanza la excepción por correo vacío
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.enviarMailDeRecuperacion(correoVacio);
        });

        assertEquals("Correo vacio.", exception.getMessage());
    }

    // Test para enviarMailDeRecuperacion - Correo no encontrado
    @Test
    void testEnviarMailDeRecuperacion_EmailNotFound() {
        // Simular el correo no existente
        String correo = "user@example.com";

        // Simular que el usuario no es encontrado en la base de datos
        when(usuarioRepository.findByEmail(correo)).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción por correo no encontrado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.enviarMailDeRecuperacion(correo);
        });

        assertEquals("No hemos encontrado tu correo electrónico.", exception.getMessage());

        // Verificar que el repositorio fue llamado para buscar el correo
        verify(usuarioRepository, times(1)).findByEmail(correo);
    }

    // Test para enviarMailDeRecuperacion - Usuario eliminado
    @Test
    void testEnviarMailDeRecuperacion_UserDeleted() {
        // Simular el correo existente
        String correo = "user@example.com";

        // Simular el usuario eliminado
        Usuario usuarioEliminado = new Usuario();
        usuarioEliminado.setDeletedAt(Instant.now());

        // Simular que el usuario es encontrado pero ha sido eliminado
        when(usuarioRepository.findByEmail(correo)).thenReturn(Optional.of(usuarioEliminado));

        // Verificar que se lanza la excepción por usuario eliminado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.enviarMailDeRecuperacion(correo);
        });

        assertEquals("Usuario ya ha sido eliminado.", exception.getMessage());

        // Verificar que el repositorio fue llamado para buscar el correo
        verify(usuarioRepository, times(1)).findByEmail(correo);
    }

    // Test para enviarMailDeRecuperacion - Éxito en el envío del correo
    @Test
    void testEnviarMailDeRecuperacion_Success() throws Exception {
        String correo = "user@example.com";

        Usuario usuario = new Usuario();
        usuario.setEmail(correo);
        usuario.setId(1L);

        when(usuarioRepository.findByEmail(correo)).thenReturn(Optional.of(usuario));

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        when(usuarioRepository.save(usuarioCaptor.capture())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            //u.setCodigoRecuperacion(UUID.randomUUID().toString());
            return u;
        });

        // Llamar al método y verificar el resultado exitoso
        String resultado = usuarioService.enviarMailDeRecuperacion(correo);
        assertEquals("Te hemos enviado un correo electrónico con las instrucciones para recuperar tu cuenta. Por favor revisa tu bandeja de entrada.", resultado);

        // Verificar que el código de recuperación ha sido asignado
        Usuario usuarioGuardado = usuarioCaptor.getValue();
        assertNotNull(usuarioGuardado.getCodigoRecuperacion());

        // Verificar que el correo de recuperación fue enviado usando el MailService
        verify(mailService, times(1)).enviarCorreoEnSegundoPlano(eq(correo), eq(usuarioGuardado.getCodigoRecuperacion()), eq(2));
        verify(usuarioRepository, times(1)).save(usuarioGuardado);
    }

    // Test para enviarMailDeRecuperacion - Fallo en el envío del correo
    @Test
    void testEnviarMailDeRecuperacion_FailedToSendEmail() throws Exception {
        String correo = "user@example.com";

        Usuario usuario = new Usuario();
        usuario.setEmail(correo);
        usuario.setId(1L);

        when(usuarioRepository.findByEmail(correo)).thenReturn(Optional.of(usuario));

        // Simular que el código de recuperación no se guarda correctamente (es null)
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setCodigoRecuperacion(null);  // Simular fallo al asignar el código
            return u;
        });

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.enviarMailDeRecuperacion(correo);
        });

        // Verificar que se lanza la excepción correcta
        assertEquals("No hemos podido enviar el correo electrónico. Intentalo más tarde.", exception.getMessage());

        // Verificar que el repositorio fue llamado para guardar el usuario
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        // Simular que no se llamó al servicio de correo ya que falló la asignación del código
        verify(mailService, times(0)).enviarCorreoEnSegundoPlano(anyString(), anyString(), anyInt());
    }

     @Test
    void testGetUsuariosByRol_Success() throws Exception {
        Rol rol = new Rol();
        rol.setNombre("AYUDANTE");

        Usuario usuario1 = new Usuario();
        usuario1.setEmail("user1@example.com");

        Usuario usuario2 = new Usuario();
        usuario2.setEmail("user2@example.com");

        List<Usuario> usuarios = List.of(usuario1, usuario2);

        when(rolService.getRol("AYUDANTE")).thenReturn(rol);
        when(usuarioRolRepository.findUsuariosByRolNombre("AYUDANTE")).thenReturn(usuarios);

        List<Usuario> result = usuarioService.getUsuariosByRol(rol);

        assertEquals(2, result.size());
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("user2@example.com", result.get(1).getEmail());

        verify(rolService, times(1)).getRol("AYUDANTE");
        verify(usuarioRolRepository, times(1)).findUsuariosByRolNombre("AYUDANTE");
    }

    @Test
    void testGetUsuariosByRol_InvalidRole() throws Exception {
        Rol rol = new Rol();
        rol.setNombre(null);  // Simulamos un rol con nombre nulo

        // Simulamos una violación de validación para el campo `nombre`
        ConstraintViolation<Rol> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("El nombre del rol no puede ser nulo");
        Set<ConstraintViolation<Rol>> violations = new HashSet<>();
        violations.add((ConstraintViolation<Rol>) violation);

        // Configuramos el validator para devolver esta violación cuando se valida `nombre`
        when(validator.validateProperty(rol, "nombre")).thenReturn(violations);

        // Llamamos al método y verificamos que lanza la excepción correcta
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.getUsuariosByRol(rol);
        });

        assertEquals("El nombre del rol no puede ser nulo\n", exception.getMessage());
    }

    @Test
    void testActualizarHorariosUsuario_Success() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");

        HorariosUsuarioRequest horariosRequest = new HorariosUsuarioRequest();
        horariosRequest.setEmailUsuario("user@example.com");

        HorarioAtencionUsuario horario1 = new HorarioAtencionUsuario();
        horario1.setHoraInicio(LocalTime.of(8, 0));
        horario1.setHoraFinal(LocalTime.of(10, 0));
        horario1.setDiaAtencion(new DiaAtencion("Lunes"));

        HorarioAtencionUsuario horario2 = new HorarioAtencionUsuario();
        horario2.setHoraInicio(LocalTime.of(14, 0));
        horario2.setHoraFinal(LocalTime.of(16, 0));
        horario2.setDiaAtencion(new DiaAtencion("Martes"));

        List<HorarioAtencionUsuario> horarios = List.of(horario1, horario2);
        horariosRequest.setHorariosAtencionUsuario(horarios);

        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.actualizarHorariosUsuario(horariosRequest);

        assertNotNull(result);
        assertEquals(2, result.getHorariosAtencionUsuario().size());
        assertEquals("08:00", result.getHorariosAtencionUsuario().get(0).getHoraInicio().toString());
        assertEquals("Lunes", result.getHorariosAtencionUsuario().get(0).getDiaAtencion().getNombre());

        verify(usuarioRepository, times(1)).findByEmail("user@example.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testActualizarHorariosUsuario_UserNotFound() {
        HorariosUsuarioRequest horariosRequest = new HorariosUsuarioRequest();
        horariosRequest.setEmailUsuario("user@example.com");

        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.actualizarHorariosUsuario(horariosRequest);
        });

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testActualizarHorariosUsuario_UserDeleted() {
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setDeletedAt(Instant.now()); // Usuario eliminado

        HorariosUsuarioRequest horariosRequest = new HorariosUsuarioRequest();
        horariosRequest.setEmailUsuario("user@example.com");

        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.actualizarHorariosUsuario(horariosRequest);
        });

        assertEquals("Usuario eliminado.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testVerificarUsuario_CodigoInvalido() {
        String codigoVerificacion = "invalid-code";

        // Simulamos que el código de verificación no se encuentra
        when(usuarioRepository.findByCodigoVerificacion(codigoVerificacion)).thenReturn(Optional.empty());

        // Verificamos que se lanza la excepción con el mensaje esperado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.verificarUsuario(codigoVerificacion);
        });

        assertEquals("Tu código de autorización invalido.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByCodigoVerificacion(codigoVerificacion);
    }

    @Test
    void testVerificarUsuario_UsuarioEliminado() {
        String codigoVerificacion = "valid-code";

        // Simulamos un usuario que ha sido eliminado
        Usuario usuario = new Usuario();
        usuario.setDeletedAt(Instant.now());

        when(usuarioRepository.findByCodigoVerificacion(codigoVerificacion)).thenReturn(Optional.of(usuario));

        // Verificamos que se lanza la excepción con el mensaje esperado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.verificarUsuario(codigoVerificacion);
        });

        assertEquals("Usuario ya ha sido eliminado.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByCodigoVerificacion(codigoVerificacion);
    }

    @Test
    void testVerificarUsuario_FalloAlActualizar() {
        String codigoVerificacion = "valid-code";

        // Simulamos un usuario válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigoVerificacion(codigoVerificacion);

        // Simulamos que el usuario se encuentra
        when(usuarioRepository.findByCodigoVerificacion(codigoVerificacion)).thenReturn(Optional.of(usuario));

        // Simulamos que la actualización falla (retorna un usuario con ID diferente)
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(2L);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        // Verificamos que se lanza la excepción con el mensaje esperado
        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.verificarUsuario(codigoVerificacion);
        });

        assertEquals("No pudimos verificar tu usuario, inténtalo más tarde.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByCodigoVerificacion(codigoVerificacion);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testVerificarUsuario_Exitoso() throws Exception {
        String codigoVerificacion = "valid-code";

        // Simulamos un usuario válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigoVerificacion(codigoVerificacion);

        when(usuarioRepository.findByCodigoVerificacion(codigoVerificacion)).thenReturn(Optional.of(usuario));

        // Simulamos que la actualización es exitosa
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        when(usuarioRepository.save(usuarioCaptor.capture())).thenReturn(usuario);

        String resultado = usuarioService.verificarUsuario(codigoVerificacion);

        // Verificamos que el mensaje de éxito sea el esperado
        assertEquals("Se verifico tu usuario con exito.", resultado);

        // Verificamos que los cambios en el usuario fueron los esperados
        Usuario usuarioActualizado = usuarioCaptor.getValue();
        assertNull(usuarioActualizado.getCodigoVerificacion());
        assertTrue(usuarioActualizado.isVerificado());

        // Verificamos interacciones con el repositorio
        verify(usuarioRepository, times(1)).findByCodigoVerificacion(codigoVerificacion);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testCambiarTwoFactor_ActivacionYaHabilitada() {
        TwoFactorActivate cambio = new TwoFactorActivate();
        cambio.setId(1L);
        cambio.setActivacion(true);
        String usuarioString = "authUser@example.com";

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setTwoFactorEnabled(true);

        this.simulateAuthenticatedUser(usuarioString);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.cambiarTwoFactor(cambio, usuarioString);
        });

        assertEquals("Esto ya esta activado.", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testCambiarTwoFactor_DesactivacionYaDeshabilitada() {
        TwoFactorActivate cambio = new TwoFactorActivate();
        cambio.setId(1L);
        cambio.setActivacion(false);
        String usuarioString = "authUser@example.com";

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setTwoFactorEnabled(false);

        this.simulateAuthenticatedUser(usuarioString);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.cambiarTwoFactor(cambio, usuarioString);
        });

        assertEquals("Esto ya esta desactivado.", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testCambiarTwoFactor_ErrorAlActualizar() {
        TwoFactorActivate cambio = new TwoFactorActivate();
        cambio.setId(1L);
        cambio.setActivacion(true);
        String usuarioString = "authUser@example.com";

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setTwoFactorEnabled(false);

        this.simulateAuthenticatedUser(usuarioString);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Simula un fallo en la actualización (retorna un usuario con ID 0)
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(0L);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.cambiarTwoFactor(cambio, usuarioString);
        });

        assertEquals("Error al tratar de actualizar el two factor.", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testCambiarTwoFactor_ActivacionExitosa() throws Exception {
        TwoFactorActivate cambio = new TwoFactorActivate();
        cambio.setId(1L);
        cambio.setActivacion(true);
        String usuarioString = "authUser@example.com";

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setTwoFactorEnabled(false);

        this.simulateAuthenticatedUser(usuarioString);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setTwoFactorEnabled(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        String resultado = usuarioService.cambiarTwoFactor(cambio, usuarioString);

        assertEquals("Se activo el two factor con exito", resultado);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testCambiarTwoFactor_DesactivacionExitosa() throws Exception {
        TwoFactorActivate cambio = new TwoFactorActivate();
        cambio.setId(1L);
        cambio.setActivacion(false);
        String usuarioString = "authUser@example.com";

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setTwoFactorEnabled(true);

        this.simulateAuthenticatedUser(usuarioString);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setTwoFactorEnabled(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        String resultado = usuarioService.cambiarTwoFactor(cambio, usuarioString);

        assertEquals("Se desactivo el two factor con exito", resultado);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

@Test
void testIniciarSesion_2F_UserNotFound() {
    // Simular el objeto de log
    Usuario log = new Usuario();
    log.setEmail("user@example.com");
    log.setPassword("password");

    // Simular que el usuario no se encuentra
    when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

    // Verificar que se lanza la excepción
    Exception exception = assertThrows(Exception.class, () -> {
        usuarioService.iniciarSesion(log);
    });

    assertEquals("Correo electronico incorrecto.", exception.getMessage());
}

@Test
void testIniciarSesion_2F_UserDeleted() {
    // Simular el objeto de log
    Usuario log = new Usuario();
    log.setEmail("user@example.com");
    log.setPassword("password");

    // Simular un usuario eliminado
    Usuario usuario = new Usuario();
    usuario.setDeletedAt(Instant.now());

    // Simular que el usuario es encontrado pero ha sido eliminado
    when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(usuario));

    // Verificar que se lanza la excepción
    Exception exception = assertThrows(Exception.class, () -> {
        usuarioService.iniciarSesion(log);
    });

    assertEquals("Usuario ya ha sido eliminado.", exception.getMessage());
}
}
