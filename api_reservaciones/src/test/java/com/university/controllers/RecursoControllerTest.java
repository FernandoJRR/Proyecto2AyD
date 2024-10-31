package com.university.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import com.university.models.EstadoReservacion;
import com.university.models.Factura;
import com.university.models.HorarioAtencionUsuario;
import com.university.models.MetodoPago;
import com.university.models.Recurso;
import com.university.models.Reservacion;
import com.university.models.Rol;
import com.university.models.Usuario;
import com.university.models.dto.LoginDto;
import com.university.models.request.PasswordChange;
import com.university.models.request.TwoFactorActivate;
import com.university.models.request.VerifyUserRequest;
import com.university.models.request.CreateCancelacionDto;
import com.university.models.request.CreateReservacionDto;
import com.university.models.request.CreateUsuarioDto;
import com.university.models.request.HorariosUsuarioRequest;
import com.university.services.EstadoReservacionService;
import com.university.services.FacturaService;
import com.university.services.MetodoPagoService;
import com.university.services.RecursoService;
import com.university.services.ReservacionService;
import com.university.services.UsuarioService;
import com.university.transformers.ApiBaseTransformer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.time.LocalTime;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class RecursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RecursoService recursoService;

    @InjectMocks
    private RecursoController recursoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recursoController).build();
    }

    @Test
    void testGetRecursos_Success() throws Exception {
        // Configurar lista de recursos simulada
        List<Recurso> recursos = Arrays.asList(new Recurso("Recurso 1"), new Recurso("Recurso 2"));
        when(recursoService.getRecursos()).thenReturn(recursos);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(get("/api/recurso/public/getRecursos")
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

        verify(recursoService, times(1)).getRecursos();
    }

    @Test
    void testGetRecursos_Failure() throws Exception {
        when(recursoService.getRecursos()).thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/api/recurso/public/getRecursos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error interno"));

        verify(recursoService, times(1)).getRecursos();
    }

    @Test
    void testGetRecurso_Success() throws Exception {
        // Configurar recurso simulado
        Recurso recurso = new Recurso("Recurso 1");
        recurso.setId(1L);

        // Simular respuesta del servicio
        when(recursoService.getRecurso(1L)).thenReturn(recurso);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(get("/api/recurso/private/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Recurso data = objectMapper.convertValue(response.getData(), Recurso.class);
        assertEquals("Recurso 1", data.getNombre());

        verify(recursoService, times(1)).getRecurso(1L);
    }

    @Test
    void testGetRecurso_NotFound() throws Exception {
        // Simular excepción cuando el recurso no se encuentra
        when(recursoService.getRecurso(1L)).thenThrow(new Exception("Recurso no encontrado"));

        // Realizar la solicitud y verificar el estado
        mockMvc.perform(get("/api/recurso/private/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));

        verify(recursoService, times(1)).getRecurso(1L);
    }

    @Test
    void testCrearRecurso_Success() throws Exception {
        // Simular un recurso a crear
        Recurso recurso = new Recurso("Nuevo Recurso");

        // Simular la respuesta del servicio
        when(recursoService.createRecurso(any(Recurso.class))).thenReturn(recurso);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(post("/api/recurso/private/crearRecurso")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(recurso)))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Recurso data = objectMapper.convertValue(response.getData(), Recurso.class);
        assertEquals("Nuevo Recurso", data.getNombre());

        verify(recursoService, times(1)).createRecurso(any(Recurso.class));
    }

    @Test
    void testCrearRecurso_Failure() throws Exception {
        // Simular un recurso a crear
        Recurso recurso = new Recurso("Recurso Inválido");

        // Simular excepción al crear el recurso
        when(recursoService.createRecurso(any(Recurso.class))).thenThrow(new Exception("Error al crear recurso"));

        // Realizar la solicitud y verificar el estado
        mockMvc.perform(post("/api/recurso/private/crearRecurso")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(recurso)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al crear recurso"));

        verify(recursoService, times(1)).createRecurso(any(Recurso.class));
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
