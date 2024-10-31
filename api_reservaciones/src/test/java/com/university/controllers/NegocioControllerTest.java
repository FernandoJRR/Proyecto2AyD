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
import com.university.models.Negocio;
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
import com.university.services.NegocioService;
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
public class NegocioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private NegocioService negocioService;

    @InjectMocks
    private NegocioController negocioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(negocioController).build();
    }

    @Test
    void testGetNegocio_Success() throws Exception {
        // Configurar negocio simulado
        Negocio negocio = new Negocio();
        negocio.setId(1L);
        negocio.setNombre("Negocio 1");

        // Simular respuesta del servicio
        when(negocioService.getNegocio(1L)).thenReturn(negocio);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(get("/api/negocio/public/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Negocio data = objectMapper.convertValue(response.getData(), Negocio.class);
        assertEquals("Negocio 1", data.getNombre());

        verify(negocioService, times(1)).getNegocio(1L);
    }

    @Test
    void testGetNegocio_NotFound() throws Exception {
        // Simular excepción cuando el negocio no se encuentra
        when(negocioService.getNegocio(1L)).thenThrow(new Exception("Negocio no encontrado"));

        // Realizar la solicitud y verificar el estado
        mockMvc.perform(get("/api/negocio/public/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Negocio no encontrado"));

        verify(negocioService, times(1)).getNegocio(1L);
    }

    @Test
    void testGetNegocios_Success() throws Exception {
        // Configurar lista de negocios simulada
        List<Negocio> negocios = Arrays.asList(new Negocio("Negocio 1"), new Negocio("Negocio 2"));
        when(negocioService.getNegocios()).thenReturn(negocios);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(get("/api/negocio/public/getNegocios")
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

        verify(negocioService, times(1)).getNegocios();
    }

    @Test
    void testGetNegocios_Failure() throws Exception {
        when(negocioService.getNegocios()).thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/api/negocio/public/getNegocios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error interno"));

        verify(negocioService, times(1)).getNegocios();
    }

    @Test
    void testCrearNegocio_Success() throws Exception {
        // Simular un negocio a crear
        Negocio negocio = new Negocio("Nuevo Negocio");

        // Simular la respuesta del servicio
        when(negocioService.createNegocio(any(Negocio.class))).thenReturn(negocio);

        // Realizar la solicitud y verificar la respuesta
        MvcResult result = mockMvc.perform(post("/api/negocio/private/crearNegocio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(negocio)))
                .andExpect(status().isOk())
                .andReturn();

        // Verificar respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        Negocio data = objectMapper.convertValue(response.getData(), Negocio.class);
        assertEquals("Nuevo Negocio", data.getNombre());

        verify(negocioService, times(1)).createNegocio(any(Negocio.class));
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
