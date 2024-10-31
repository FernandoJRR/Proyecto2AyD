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
import com.university.models.request.CreateServicioDto;
import com.university.models.request.PermisoRolRequest;
import com.university.models.request.ServicioRolRequest;
import com.university.services.RolService;
import com.university.services.ServicioService;
import com.university.transformers.ApiBaseTransformer;

import java.util.List;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ServicioService servicioService;

    @InjectMocks
    private ServicioController servicioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(servicioController).build();
    }

    @Test
    void testGetServicios_Success() throws Exception {
        // Configurar lista de servicios simulada
        List<Servicio> servicios = Arrays.asList(new Servicio("Servicio 1"), new Servicio("Servicio 2"));
        when(servicioService.getServicios()).thenReturn(servicios);

        // Realizar la solicitud y verificar la respuesta
        mockMvc.perform(get("/api/servicio/public/getServicios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(servicioService, times(1)).getServicios();
    }

    @Test
    void testGetServicios_Failure() throws Exception {
        // Simular excepción al obtener servicios
        when(servicioService.getServicios()).thenThrow(new RuntimeException("Error al obtener servicios"));

        // Realizar la solicitud y verificar el estado de error
        mockMvc.perform(get("/api/servicio/public/getServicios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al obtener servicios"));

        verify(servicioService, times(1)).getServicios();
    }

    @Test
    void testGetServicio_Success() throws Exception {
        // Configurar servicio simulado
        Servicio servicio = new Servicio("Servicio de Prueba");
        servicio.setId(1L); // Asegúrate de que el ID esté configurado

        // Simular respuesta del servicio
        when(servicioService.getServicio(1L)).thenReturn(servicio);

        // Realizar la solicitud y verificar la respuesta
        mockMvc.perform(get("/api/servicio/public/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.nombre").value("Servicio de Prueba"));

        verify(servicioService, times(1)).getServicio(1L);
    }

    @Test
    void testGetServicio_NotFound() throws Exception {
        // Simular excepción cuando el servicio no se encuentra
        when(servicioService.getServicio(1L)).thenThrow(new Exception("Servicio no encontrado"));

        // Realizar la solicitud y verificar el estado
        mockMvc.perform(get("/api/servicio/public/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Servicio no encontrado"));

        verify(servicioService, times(1)).getServicio(1L);
    }

    @Test
    void testCrearServicio_Success() throws Exception {
        // Configurar DTO de entrada y servicio de respuesta
        CreateServicioDto servicioDto = new CreateServicioDto();
        Servicio servicio = new Servicio("Nuevo Servicio");
        servicioDto.setServicio(servicio);

        when(servicioService.createServicio(any(CreateServicioDto.class))).thenReturn(servicio);

        // Realizar la solicitud y verificar la respuesta
        mockMvc.perform(post("/api/servicio/private/crearServicio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(servicioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo Servicio"));

        verify(servicioService, times(1)).createServicio(any(CreateServicioDto.class));
    }

    @Test
    void testCrearServicio_Failure() throws Exception {
        // Configurar DTO de entrada sin datos válidos
        CreateServicioDto servicioDto = new CreateServicioDto();
        servicioDto.setServicio(new Servicio("Servicio Inválido"));

        // Simular excepción al crear el servicio
        when(servicioService.createServicio(any(CreateServicioDto.class)))
                .thenThrow(new Exception("Error al crear servicio"));

        // Realizar la solicitud y verificar el estado de error
        mockMvc.perform(post("/api/servicio/private/crearServicio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(servicioDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al crear servicio"));

        verify(servicioService, times(1)).createServicio(any(CreateServicioDto.class));
    }

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
