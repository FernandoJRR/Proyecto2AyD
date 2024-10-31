package com.university.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.models.HorarioAtencionUsuario;
import com.university.models.Rol;
import com.university.models.Usuario;
import com.university.models.dto.LoginDto;
import com.university.models.request.PasswordChange;
import com.university.models.request.TwoFactorActivate;
import com.university.models.request.VerifyUserRequest;
import com.university.models.request.CreateUsuarioDto;
import com.university.models.request.HorariosUsuarioRequest;
import com.university.services.UsuarioService;
import com.university.transformers.ApiBaseTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.time.LocalTime;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // Test para obtener usuario por ID (GET /api/usuario/protected/{id})
    @Test
    void testGetUsuarioById_Success() throws Exception {
        // Simular un objeto Usuario válido
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");

        // Simular la respuesta del servicio
        when(usuarioService.getUsuario(1L)).thenReturn(usuario);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(get("/api/usuario/public/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el campo 'status'
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        Usuario data = objectMapper.convertValue(response.getData(), Usuario.class); // Extraer el usuario de 'data'
        assertEquals(1L, data.getId()); // Verificar que el ID del usuario sea 1
        assertEquals("test@example.com", data.getEmail());

        // Verificar que el servicio fue llamado una vez
        verify(usuarioService, times(1)).getUsuario(1L);
    }

    // Test para obtener usuario por ID - Usuario no encontrado (GET
    // /api/usuario/protected/{id})
    @Test
    void testGetUsuarioById_NotFound() throws Exception {
        // Simular que el servicio lanza una excepción al no encontrar el usuario
        when(usuarioService.getUsuario(1L)).thenThrow(new Exception("Usuario no encontrado."));

        // Realizar la solicitud y verificar el estado de la respuesta
        MvcResult result = mockMvc.perform(get("/api/usuario/public/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(400, response.getCode()); // Verificar el campo 'code'
        assertEquals("Usuario no encontrado.", response.getError()); // Verificar el mensaje de error

        // Verificar que el servicio fue llamado una vez
        verify(usuarioService, times(1)).getUsuario(1L);
    }

    // Test para crear un nuevo usuario (POST /api/usuario/public/crearUsuario)
    @Test
    void testCrearUsuario_Success() throws Exception {
        // Simular un objeto Usuario válido para la creación
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");

        LoginDto loginDto = new LoginDto(usuario, "mocked-jwt-token");

        // Simular la respuesta del servicio
        when(usuarioService.crearUsuarioNormal(any(Usuario.class))).thenReturn(loginDto);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(post("/api/usuario/public/crearUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk()) // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el campo 'status'
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        LoginDto data = objectMapper.convertValue(response.getData(), LoginDto.class); // Extraer el LoginDto de 'data'

        // Verificar el contenido del usuario dentro de LoginDto
        assertEquals("test@example.com", data.getUsuario().getEmail());

        // Verificar que el servicio fue llamado una vez
        verify(usuarioService, times(1)).crearUsuarioNormal(any(Usuario.class));
    }

    // Helper method to convert object to JSON string
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Registro del módulo JavaTime
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetUsuarios_Success() throws Exception {
        // Simular una lista de usuarios
        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setEmail("user1@example.com");
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setEmail("user2@example.com");
        usuarios.add(usuario1);
        usuarios.add(usuario2);

        // Simular la respuesta del servicio
        when(usuarioService.getUsuarios()).thenReturn(usuarios);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(get("/api/usuario/private/getUsuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el campo 'code'
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        List<?> data = objectMapper.convertValue(response.getData(), List.class); // Convertir 'data' a lista
        assertEquals(2, data.size()); // Verificar que la lista contiene 2 usuarios

        // Verificar que el servicio fue llamado una vez
        verify(usuarioService, times(1)).getUsuarios();
    }

    @Test
    void testEnviarMailDeRecuperacion_Success() throws Exception {
        // Simular el correo de recuperación exitoso
        String correo = "user@example.com";
        String mensaje = "Te hemos enviado un correo electrónico con las instrucciones para recuperar tu cuenta.";

        // Simular la respuesta del servicio
        when(usuarioService.enviarMailDeRecuperacion(correo)).thenReturn(mensaje);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(post("/api/usuario/public/recuperarPasswordMail")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"correoElectronico\":\"" + correo + "\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk()) // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el campo 'code'
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        assertEquals(mensaje, response.getData()); // Verificar el mensaje de recuperación

        // Verificar que el servicio fue llamado una vez
        verify(usuarioService, times(1)).enviarMailDeRecuperacion(correo);
    }

    @Test
    void testRecuperarPassword_Success() throws Exception {
        // Simular el cambio de contraseña exitoso
        PasswordChange passwordChange = new PasswordChange();
        passwordChange.setCodigo("12345");
        passwordChange.setNuevaPassword("newPassword123");
        String mensaje = "Se cambió tu contraseña con exito.";

        // Simular la respuesta del servicio
        when(usuarioService.recuperarPassword(any(PasswordChange.class))).thenReturn(mensaje);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(patch("/api/usuario/public/recuperarPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"12345\", \"nuevaPassword\":\"newPassword123\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk()) // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el campo 'code'
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        assertEquals(mensaje, response.getData()); // Verificar el mensaje de éxito

        // Verificar que el servicio fue llamado una vez
        verify(usuarioService, times(1)).recuperarPassword(any(PasswordChange.class));
    }

    @Test
    void testCambiarPassword_Success() throws Exception {
        // Simular el cambio de contraseña exitoso
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPassword("newPassword123");
        String mensaje = "Se cambió tu contraseña con exito.";

        // Simular la autenticación del usuario
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Simular la respuesta del servicio
        when(usuarioService.cambiarPassword(any(Usuario.class), anyString())).thenReturn(mensaje);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(patch("/api/usuario/public/cambioPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1, \"password\":\"newPassword123\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk()) // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el campo 'code'
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        assertEquals(mensaje, response.getData()); // Verificar el mensaje de éxito

        // Verificar que el servicio fue llamado una vez
        verify(usuarioService, times(1)).cambiarPassword(any(Usuario.class), anyString());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Crear un objeto Usuario simulado con email y contraseña
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPassword("password123");

        // Crear una respuesta de LoginDto simulada
        LoginDto loginDto = new LoginDto(usuario, "fake-jwt-token");

        // Simular la respuesta del servicio
        when(usuarioService.iniciarSesion(any(Usuario.class))).thenReturn(loginDto);

        // Realizar la solicitud de inicio de sesión
        MvcResult result = mockMvc.perform(post("/api/usuario/public/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"email\":\"test@example.com\", \"password\":\"password123\"}"))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el código de estado
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        LoginDto data = objectMapper.convertValue(response.getData(), LoginDto.class); // Extraer el LoginDto
        assertEquals("test@example.com", data.getUsuario().getEmail());
        assertEquals("fake-jwt-token", data.getJwt());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).iniciarSesion(any(Usuario.class));
    }

    @Test
    void testLogin_Failure() throws Exception {
        // Simular un caso en el que el servicio arroje una excepción por credenciales
        // incorrectas
        when(usuarioService.iniciarSesion(any(Usuario.class))).thenThrow(new Exception("Credenciales incorrectas"));

        // Realizar la solicitud de inicio de sesión
        mockMvc.perform(post("/api/usuario/public/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"email\":\"wrong@example.com\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Credenciales incorrectas")); // Verificar el mensaje de error

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).iniciarSesion(any(Usuario.class));
    }

    @Test
    void testValidateTwoFactorToken_Success() throws Exception {
        // Crear un objeto Usuario simulado con email y código de dos factores
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setTwoFactorCode("67858");

        // Crear una respuesta de LoginDto simulada
        LoginDto loginDto = new LoginDto(usuario, "fake-jwt-token");

        // Simular la respuesta del servicio
        when(usuarioService.login2FT(any(Usuario.class))).thenReturn(loginDto);

        // Realizar la solicitud de validación del código de dos factores
        MvcResult result = mockMvc.perform(post("/api/usuario/public/validateTwoFactorToken")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"email\":\"user@example.com\", \"twoFactorCode\":\"67858\"}"))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode()); // Verificar el código de estado
        assertEquals("OK", response.getMessage()); // Verificar el mensaje
        LoginDto data = objectMapper.convertValue(response.getData(), LoginDto.class); // Extraer el LoginDto
        assertEquals("user@example.com", data.getUsuario().getEmail());
        assertEquals("fake-jwt-token", data.getJwt());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).login2FT(any(Usuario.class));
    }

    @Test
    void testValidateTwoFactorToken_Failure() throws Exception {
        // Simular un caso en el que el servicio arroje una excepción por código
        // incorrecto
        when(usuarioService.login2FT(any(Usuario.class)))
                .thenThrow(new Exception("Código de autenticación incorrecto"));

        // Realizar la solicitud de validación del código de dos factores
        mockMvc.perform(post("/api/usuario/public/validateTwoFactorToken")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"email\":\"user@example.com\", \"twoFactorCode\":\"12345\"}"))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Código de autenticación incorrecto")); // Verificar el mensaje de
                                                                                             // error

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).login2FT(any(Usuario.class));
    }

    @Test
    void testCrearAdministrador_Success() throws Exception {
        // Crear un objeto Usuario simulado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("admin@example.com");

        // Simular la respuesta del servicio
        when(usuarioService.crearAdministrador(any(Usuario.class))).thenReturn(usuario);

        // Realizar la solicitud de creación de administrador
        MvcResult result = mockMvc.perform(post("/api/usuario/private/crearAdministrador")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Verificar los valores de la respuesta
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar el contenido del response
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Usuario data = objectMapper.convertValue(response.getData(), Usuario.class);
        assertEquals("admin@example.com", data.getEmail());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).crearAdministrador(any(Usuario.class));
    }

    @Test
    void testCrearAdministrador_Failure() throws Exception {
        // Simular un caso en el que el servicio arroje una excepción
        when(usuarioService.crearAdministrador(any(Usuario.class)))
                .thenThrow(new Exception("Error al crear administrador"));

        // Realizar la solicitud de creación de administrador
        mockMvc.perform(post("/api/usuario/private/crearAdministrador")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al crear administrador"));

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).crearAdministrador(any(Usuario.class));
    }

    @Test
    void testGetPerfil_Success() throws Exception {
        // Crear un objeto Usuario simulado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");

        // Simular la respuesta del servicio
        when(usuarioService.getUsuario(1L)).thenReturn(usuario);

        // Realizar la solicitud de obtención del perfil
        MvcResult result = mockMvc.perform(get("/api/usuario/private/all/perfil/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Verificar los valores de la respuesta
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Usuario data = objectMapper.convertValue(response.getData(), Usuario.class);
        assertEquals("user@example.com", data.getEmail());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).getUsuario(1L);
    }

    @Test
    void testGetPerfil_Failure() throws Exception {
        // Simular un caso en el que el servicio arroje una excepción
        when(usuarioService.getUsuario(1L)).thenThrow(new Exception("Usuario no encontrado"));

        // Realizar la solicitud de obtención del perfil
        mockMvc.perform(get("/api/usuario/private/all/perfil/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).getUsuario(1L);
    }

    @Test
    void testEliminarUsuario_Success() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Simular la respuesta del servicio
        when(usuarioService.eliminarUsuario(anyLong(), anyString())).thenReturn("Usuario eliminado con éxito");

        // Realizar la solicitud de eliminación de usuario
        MvcResult result = mockMvc.perform(delete("/api/usuario/private/eliminarUsuario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Verificar los valores de la respuesta
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        assertEquals("Usuario eliminado con éxito", response.getData());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).eliminarUsuario(anyLong(), anyString());
    }

    @Test
    void testEliminarUsuario_Failure() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Simular un caso en el que el servicio arroje una excepción
        when(usuarioService.eliminarUsuario(anyLong(), anyString()))
                .thenThrow(new Exception("Error al eliminar usuario"));

        // Realizar la solicitud de eliminación de usuario
        mockMvc.perform(delete("/api/usuario/private/eliminarUsuario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al eliminar usuario"));

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).eliminarUsuario(anyLong(), anyString());
    }

    @Test
    void testActualizarUsuarioParcial_Success() throws Exception {
        // Crear un objeto Usuario simulado
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("updated@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Simular la respuesta del servicio
        when(usuarioService.updateUsuario(any(Usuario.class), anyString())).thenReturn(usuario);

        // Realizar la solicitud de actualización parcial
        MvcResult result = mockMvc.perform(patch("/api/usuario/private/all/updateUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"email\":\"updated@example.com\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Verificar los valores de la respuesta
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Usuario data = objectMapper.convertValue(response.getData(), Usuario.class);
        assertEquals("updated@example.com", data.getEmail());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).updateUsuario(any(Usuario.class), anyString());
    }

    @Test
    void testActualizarUsuarioParcial_Failure() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Simular un caso en el que el servicio arroje una excepción
        when(usuarioService.updateUsuario(any(Usuario.class), anyString()))
                .thenThrow(new Exception("Error al actualizar usuario"));

        // Realizar la solicitud de actualización parcial
        mockMvc.perform(patch("/api/usuario/private/all/updateUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"email\":\"updated@example.com\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al actualizar usuario"));

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).updateUsuario(any(Usuario.class), anyString());
    }

    @Test
    void testCambiarTwoFactor_Success() throws Exception {
        // Crear un objeto TwoFactorActivate simulado
        TwoFactorActivate updates = new TwoFactorActivate();
        updates.setActivacion(true);

        String mensajeExito = "Two-factor authentication actualizado correctamente.";

        // Simular autenticación
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Simular respuesta del servicio
        when(usuarioService.cambiarTwoFactor(any(TwoFactorActivate.class), anyString())).thenReturn(mensajeExito);

        // Realizar la solicitud de actualización de two-factor
        MvcResult result = mockMvc.perform(patch("/api/usuario/public/cambiarTwoFactor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updates))
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        assertEquals(mensajeExito, response.getData());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).cambiarTwoFactor(any(TwoFactorActivate.class), anyString());
    }

    @Test
    void testCambiarTwoFactor_Failure() throws Exception {
        // Crear un objeto TwoFactorActivate simulado
        TwoFactorActivate updates = new TwoFactorActivate();
        updates.setActivacion(false);

        // Simular autenticación
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Simular excepción en el servicio
        when(usuarioService.cambiarTwoFactor(any(TwoFactorActivate.class), anyString()))
                .thenThrow(new Exception("Error al actualizar two-factor"));

        // Realizar la solicitud de actualización de two-factor
        mockMvc.perform(patch("/api/usuario/public/cambiarTwoFactor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updates))
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al actualizar two-factor"));

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).cambiarTwoFactor(any(TwoFactorActivate.class), anyString());
    }

    @Test
    void testActualizarHorariosUsuario_Success() throws Exception {
        // Crear un objeto HorariosUsuarioRequest simulado
        HorariosUsuarioRequest updates = new HorariosUsuarioRequest();
        updates.setEmailUsuario("user@example.com");

        HorarioAtencionUsuario horario1 = new HorarioAtencionUsuario();
        horario1.setId(1L);
        horario1.setHoraInicio(LocalTime.of(9, 0));
        horario1.setHoraFinal(LocalTime.of(17, 0));
        HorarioAtencionUsuario horario2 = new HorarioAtencionUsuario();
        horario2.setId(2L);
        horario2.setHoraInicio(LocalTime.of(10, 0));
        horario2.setHoraFinal(LocalTime.of(18, 0));
        updates.setHorariosAtencionUsuario(Arrays.asList(horario1, horario2));

        // Crear un objeto Usuario simulado
        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");

        // Simular respuesta del servicio
        when(usuarioService.actualizarHorariosUsuario(any(HorariosUsuarioRequest.class))).thenReturn(usuario);

        // Realizar la solicitud de actualización de horarios
        MvcResult result = mockMvc.perform(patch("/api/usuario/private/actualizarHorariosUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updates))
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Convertir la respuesta de String a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Usuario data = objectMapper.convertValue(response.getData(), Usuario.class);
        assertEquals("user@example.com", data.getEmail());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).actualizarHorariosUsuario(any(HorariosUsuarioRequest.class));
    }

    @Test
    void testActualizarHorariosUsuario_Failure() throws Exception {
        // Crear un objeto HorariosUsuarioRequest simulado
        HorariosUsuarioRequest updates = new HorariosUsuarioRequest();
        updates.setEmailUsuario("user@example.com");

        // Simular excepción en el servicio
        when(usuarioService.actualizarHorariosUsuario(any(HorariosUsuarioRequest.class)))
                .thenThrow(new Exception("Error al actualizar horarios"));

        // Realizar la solicitud de actualización de horarios
        mockMvc.perform(patch("/api/usuario/private/actualizarHorariosUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updates))
                .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al actualizar horarios"));

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).actualizarHorariosUsuario(any(HorariosUsuarioRequest.class));
    }

        @Test
    void testCrearAyudante_Success() throws Exception {
        // Configurar el DTO de entrada y el usuario de respuesta
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("ayudante@example.com");

        Rol rolAyudante = new Rol();
        rolAyudante.setNombre("Ayudante");

        CreateUsuarioDto crearDto = new CreateUsuarioDto();
        crearDto.setUsuario(usuario);
        crearDto.setRoles(Collections.singletonList(rolAyudante));

        // Simular la respuesta del servicio
        when(usuarioService.crearAyudante(any(CreateUsuarioDto.class))).thenReturn(usuario);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(post("/api/usuario/private/crearAyudante")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(crearDto)))
                .andExpect(status().isOk())  // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());  // Verificar el código de estado
        assertEquals("OK", response.getMessage());  // Verificar el mensaje
        Usuario data = objectMapper.convertValue(response.getData(), Usuario.class);  // Extraer el usuario
        assertEquals("ayudante@example.com", data.getEmail());

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).crearAyudante(any(CreateUsuarioDto.class));
    }

    @Test
    void testCrearAyudante_EmailYaExiste() throws Exception {
        // Configurar el DTO de entrada
        Usuario usuario = new Usuario();
        usuario.setEmail("ayudante@example.com");

        Rol rolAyudante = new Rol();
        rolAyudante.setNombre("Ayudante");

        CreateUsuarioDto crearDto = new CreateUsuarioDto();
        crearDto.setUsuario(usuario);
        crearDto.setRoles(Collections.singletonList(rolAyudante));

        // Simular un caso en el que el servicio arroje una excepción de email duplicado
        when(usuarioService.crearAyudante(any(CreateUsuarioDto.class)))
                .thenThrow(new Exception("El Email ya existe."));

        // Realizar la solicitud y verificar el estado de la respuesta
        mockMvc.perform(post("/api/usuario/private/crearAyudante")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(crearDto)))
                .andExpect(status().isBadRequest())  // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("El Email ya existe."));  // Verificar el mensaje de error

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).crearAyudante(any(CreateUsuarioDto.class));
    }

        @Test
    void testVerificarUsuario_Success() throws Exception {
        // Configurar el DTO de entrada y la respuesta esperada
        String codigoVerificacion = "123456";
        VerifyUserRequest verifyRequest = new VerifyUserRequest(codigoVerificacion);
        String mensajeExito = "Se verifico tu usuario con exito.";

        // Simular la respuesta del servicio
        when(usuarioService.verificarUsuario(codigoVerificacion)).thenReturn(mensajeExito);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(patch("/api/usuario/public/verificarUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(verifyRequest)))
                .andExpect(status().isOk())  // Verifica el estado de la respuesta HTTP
                .andReturn();

        // Convertir la respuesta a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());  // Verificar el código de estado
        assertEquals("OK", response.getMessage());  // Verificar el mensaje
        assertEquals(mensajeExito, response.getData());  // Verificar el mensaje de éxito en el 'data'

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).verificarUsuario(codigoVerificacion);
    }

    @Test
    void testVerificarUsuario_CodigoInvalido() throws Exception {
        // Configurar el DTO de entrada
        String codigoVerificacion = "invalid-code";
        VerifyUserRequest verifyRequest = new VerifyUserRequest(codigoVerificacion);

        // Simular que el servicio lanza una excepción de código inválido
        when(usuarioService.verificarUsuario(codigoVerificacion))
                .thenThrow(new Exception("Tu código de autorización invalido."));

        // Realizar la solicitud y verificar el estado de la respuesta
        mockMvc.perform(patch("/api/usuario/public/verificarUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(verifyRequest)))
                .andExpect(status().isBadRequest())  // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Tu código de autorización invalido."));  // Verificar el mensaje de error

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).verificarUsuario(codigoVerificacion);
    }

    @Test
    void testVerificarUsuario_UsuarioEliminado() throws Exception {
        // Configurar el DTO de entrada
        String codigoVerificacion = "deleted-code";
        VerifyUserRequest verifyRequest = new VerifyUserRequest(codigoVerificacion);

        // Simular que el servicio lanza una excepción de usuario eliminado
        when(usuarioService.verificarUsuario(codigoVerificacion))
                .thenThrow(new Exception("Usuario ya ha sido eliminado."));

        // Realizar la solicitud y verificar el estado de la respuesta
        mockMvc.perform(patch("/api/usuario/public/verificarUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(verifyRequest)))
                .andExpect(status().isBadRequest())  // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Usuario ya ha sido eliminado."));  // Verificar el mensaje de error

        // Verificar que el servicio fue llamado correctamente
        verify(usuarioService, times(1)).verificarUsuario(codigoVerificacion);
    }
}
