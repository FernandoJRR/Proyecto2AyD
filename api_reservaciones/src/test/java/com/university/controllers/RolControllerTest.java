package com.university.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.models.Permiso;
import com.university.models.Rol;
import com.university.models.Servicio;
import com.university.models.request.CreateRolDto;
import com.university.models.request.PermisoRolRequest;
import com.university.models.request.ServicioRolRequest;
import com.university.services.RolService;
import com.university.transformers.ApiBaseTransformer;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController rolController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rolController).build();
    }

    @Test
    void testGetRol_Success() throws Exception {
        // Configurar rol simulado
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("Administrador");

        // Simular respuesta del servicio
        when(rolService.getRolById(1L)).thenReturn(rol);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(get("/api/rol/public/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Rol data = objectMapper.convertValue(response.getData(), Rol.class);
        assertEquals("Administrador", data.getNombre());

        verify(rolService, times(1)).getRolById(1L);
    }

    @Test
    void testGetRol_NotFound() throws Exception {
        // Simular excepción cuando el rol no se encuentra
        when(rolService.getRolById(1L)).thenThrow(new Exception("Rol no encontrado"));

        // Realizar la solicitud y verificar el estado
        mockMvc.perform(get("/api/rol/public/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Rol no encontrado"));

        verify(rolService, times(1)).getRolById(1L);
    }

    @Test
    void testGetRoles_Success() throws Exception {
        // Configurar lista de roles simulada
        List<Rol> roles = List.of(new Rol("Administrador"), new Rol("Usuario"));
        when(rolService.getRoles()).thenReturn(roles);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(get("/api/rol/public/getRoles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        List<?> data = objectMapper.convertValue(response.getData(), List.class);
        assertEquals(2, data.size());

        verify(rolService, times(1)).getRoles();
    }

    @Test
    void testCrearRol_Success() throws Exception {
        // Configurar DTO de entrada y rol de respuesta
        CreateRolDto rolDto = new CreateRolDto();
        Rol rol = new Rol();
        rol.setNombre("Nuevo Rol");
        rolDto.setRol(rol);
        rolDto.setPermisos(List.of(new Permiso("PERMISO_1", "Ruta1")));
        rolDto.setServicios(List.of(new Servicio("Servicio1")));
        when(rolService.createRol(any(CreateRolDto.class))).thenReturn(rol);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(post("/api/rol/private/crearRol")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(rolDto)))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Rol data = objectMapper.convertValue(response.getData(), Rol.class);
        assertEquals("Nuevo Rol", data.getNombre());

        verify(rolService, times(1)).createRol(any(CreateRolDto.class));
    }

    @Test
    void testCrearRol_Failure_NoPermisos() throws Exception {
        // Configurar DTO de entrada sin permisos
        CreateRolDto rolDto = new CreateRolDto();
        rolDto.setRol(new Rol("Rol sin Permisos"));
        rolDto.setServicios(List.of(new Servicio("Servicio1")));

        // Simular excepción de permisos faltantes
        when(rolService.createRol(any(CreateRolDto.class)))
                .thenThrow(new Exception("Debes asignar permisos al rol"));

        // Realizar la solicitud y verificar el estado de error
        mockMvc.perform(post("/api/rol/private/crearRol")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(rolDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Debes asignar permisos al rol"));

        verify(rolService, times(1)).createRol(any(CreateRolDto.class));
    }

    @Test
    void testActualizarPermisos_Success() throws Exception {
        // Configurar DTO de actualización y rol de respuesta
        PermisoRolRequest permisoRequest = new PermisoRolRequest(1L, List.of(new Permiso("PERMISO_1", "Ruta1")));
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("Rol Actualizado");
        when(rolService.actualizarPermisosRol(any(PermisoRolRequest.class))).thenReturn(rol);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(patch("/api/rol/private/actualizarPermisos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(permisoRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Rol data = objectMapper.convertValue(response.getData(), Rol.class);
        assertEquals("Rol Actualizado", data.getNombre());

        verify(rolService, times(1)).actualizarPermisosRol(any(PermisoRolRequest.class));
    }

    @Test
    void testActualizarServicios_Success() throws Exception {
        // Configurar DTO de actualización y rol de respuesta
        ServicioRolRequest servicioRequest = new ServicioRolRequest(1L, List.of(new Servicio("Servicio1")));
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("Rol con Servicios Actualizados");
        when(rolService.actualizarServiciosRol(any(ServicioRolRequest.class))).thenReturn(rol);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(patch("/api/rol/private/actualizarServicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(servicioRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Rol data = objectMapper.convertValue(response.getData(), Rol.class);
        assertEquals("Rol con Servicios Actualizados", data.getNombre());

        verify(rolService, times(1)).actualizarServiciosRol(any(ServicioRolRequest.class));
    }

    @Test
    void testActualizarPermisos_InvalidIdFormat() throws Exception {
        // Configurar PermisoRolRequest con ID en formato inválido
        PermisoRolRequest permisoRequest = new PermisoRolRequest();
        permisoRequest.setIdRol(-1L); // ID inválido
        permisoRequest.setPermisos(List.of(new Permiso("PERMISO_1", "Ruta1")));

        // Simular que el servicio lanza una excepción de formato de ID inválido
        when(rolService.actualizarPermisosRol(any(PermisoRolRequest.class)))
                .thenThrow(new NumberFormatException("Id con formato invalido"));

        // Realizar la solicitud y verificar el estado de respuesta
        mockMvc.perform(patch("/api/rol/private/actualizarPermisos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(permisoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id con formato invalido"));

        verify(rolService, times(1)).actualizarPermisosRol(any(PermisoRolRequest.class));
    }

    @Test
    void testActualizarPermisos_InternalServerError() throws Exception {
        // Configurar PermisoRolRequest válido
        PermisoRolRequest permisoRequest = new PermisoRolRequest(1L, List.of(new Permiso("PERMISO_1", "Ruta1")));

        // Simular que el servicio lanza una excepción general
        when(rolService.actualizarPermisosRol(any(PermisoRolRequest.class)))
                .thenThrow(new Exception("Error interno del servidor"));

        // Realizar la solicitud y verificar el estado de respuesta
        mockMvc.perform(patch("/api/rol/private/actualizarPermisos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(permisoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error interno del servidor"));

        verify(rolService, times(1)).actualizarPermisosRol(any(PermisoRolRequest.class));
    }

    @Test
    void testActualizarServicios_InvalidIdFormat() throws Exception {
        // Configurar ServicioRolRequest con ID en formato inválido
        ServicioRolRequest servicioRequest = new ServicioRolRequest();
        servicioRequest.setIdRol(-1L); // ID inválido
        servicioRequest.setServicios(List.of(new Servicio("Servicio1")));

        // Simular que el servicio lanza una excepción de formato de ID inválido
        when(rolService.actualizarServiciosRol(any(ServicioRolRequest.class)))
                .thenThrow(new NumberFormatException("Id con formato invalido"));

        // Realizar la solicitud y verificar el estado de respuesta
        mockMvc.perform(patch("/api/rol/private/actualizarServicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(servicioRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id con formato invalido"));

        verify(rolService, times(1)).actualizarServiciosRol(any(ServicioRolRequest.class));
    }

    @Test
    void testActualizarServicios_InternalServerError() throws Exception {
        // Configurar ServicioRolRequest válido
        ServicioRolRequest servicioRequest = new ServicioRolRequest(1L, List.of(new Servicio("Servicio1")));

        // Simular que el servicio lanza una excepción general
        when(rolService.actualizarServiciosRol(any(ServicioRolRequest.class)))
                .thenThrow(new Exception("Error interno del servidor"));

        // Realizar la solicitud y verificar el estado de respuesta
        mockMvc.perform(patch("/api/rol/private/actualizarServicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(servicioRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error interno del servidor"));

        verify(rolService, times(1)).actualizarServiciosRol(any(ServicioRolRequest.class));
    }

    // Helper method to convert object to JSON string
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
