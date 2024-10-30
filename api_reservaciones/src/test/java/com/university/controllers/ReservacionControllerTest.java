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
import com.university.services.MetodoPagoService;
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
public class ReservacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ReservacionService reservacionService;

    @Mock
    private EstadoReservacionService estadoReservacionService;

    @Mock
    private MetodoPagoService metodoPagoService;

    @InjectMocks
    private ReservacionController reservacionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservacionController).build();
    }

    // Test para obtener todos los estados de reservación
    @Test
    void testGetEstadosReservacion_Success() throws Exception {
        List<EstadoReservacion> estados = Arrays.asList(new EstadoReservacion("Activo"),
                new EstadoReservacion("Inactivo"));
        when(estadoReservacionService.getEstadosReservacion()).thenReturn(estados);

        MvcResult result = mockMvc.perform(get("/api/reservacion/public/getEstadosReservacion")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        List<?> data = objectMapper.convertValue(response.getData(), List.class);
        assertEquals(2, data.size());

        verify(estadoReservacionService, times(1)).getEstadosReservacion();
    }

    @Test
    void testGetEstadosReservacion_Failure() throws Exception {
        when(estadoReservacionService.getEstadosReservacion()).thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/api/reservacion/public/getEstadosReservacion")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error interno"));

        verify(estadoReservacionService, times(1)).getEstadosReservacion();
    }

    // Test para obtener todos los métodos de pago
    @Test
    void testGetMetodosPago_Success() throws Exception {
        List<MetodoPago> metodos = Arrays.asList(new MetodoPago("Tarjeta"), new MetodoPago("Efectivo"));
        when(metodoPagoService.getMetodosPago()).thenReturn(metodos);

        MvcResult result = mockMvc.perform(get("/api/reservacion/public/getMetodosPago")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        List<?> data = objectMapper.convertValue(response.getData(), List.class);
        assertEquals(2, data.size());

        verify(metodoPagoService, times(1)).getMetodosPago();
    }

    @Test
    void testGetMetodosPago_Failure() throws Exception {
        when(metodoPagoService.getMetodosPago()).thenThrow(new RuntimeException("Error al obtener métodos de pago"));

        mockMvc.perform(get("/api/reservacion/public/getMetodosPago")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error"))
                .andExpect(jsonPath("$.error").value("Error al obtener métodos de pago"));

        verify(metodoPagoService, times(1)).getMetodosPago();
    }

    // Test para crear una nueva reservación
    @Test
    void testCreateReservacion_Success() throws Exception {
        // Simular un objeto Reservacion válido para la creación
        Reservacion reservacion = new Reservacion();
        reservacion.setId(1L); // Asegúrate de que el ID esté configurado

        // Simular la respuesta del servicio
        when(reservacionService.crearReservacion(
                anyString(), anyLong(), anyLong(), anyString(), anyString(), any(),
                any(), any(), any(), anyString(), anyString(), anyFloat())).thenReturn(reservacion);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(post("/api/reservacion/public/crearReservacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"emailUsuario\":\"cliente@example.com\", \"idEncargado\":1, \"idServicio\":1, \"nombreUnidadRecurso\":\"Recurso1\", \"nombreRecurso\":\"Recurso2\", \"horaInicio\":\"10:00\", \"horaFinal\":\"11:00\", \"fecha\":\"2024-11-01\", \"nombreMetodoPago\":\"Tarjeta\", \"numeroPago\":\"1234\", \"montoPago\":100.0}"))
                .andExpect(status().isCreated())
                .andReturn();

        // Verificar la respuesta JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        assertEquals(201, response.getCode());
        assertEquals("Reservación creada", response.getMessage());
        Reservacion data = objectMapper.convertValue(response.getData(), Reservacion.class);
        assertEquals(1L, data.getId()); // Verificar que el ID de la reservación sea 1
    }

    @Test
    void testCreateReservacion_Failure() throws Exception {
        // Simular excepción en el servicio para una falla
        when(reservacionService.crearReservacion(
                anyString(), anyLong(), anyLong(), anyString(), anyString(), any(),
                any(), any(), any(), anyString(), anyString(), anyFloat()))
                .thenThrow(new Exception("Error al crear la reservación"));

        // Realizar la solicitud y verificar el estado de la respuesta
        mockMvc.perform(post("/api/reservacion/public/crearReservacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"emailUsuario\":\"cliente@example.com\", \"idEncargado\":1, \"idServicio\":1, \"nombreUnidadRecurso\":\"Recurso1\", \"nombreRecurso\":\"Recurso2\", \"horaInicio\":\"10:00\", \"horaFinal\":\"11:00\", \"fecha\":\"2024-11-01\", \"nombreMetodoPago\":\"Tarjeta\", \"numeroPago\":\"1234\", \"montoPago\":100.0}"))
                .andExpect(status().isBadRequest()) // Verificar que el estado de respuesta es 400 BAD REQUEST
                .andExpect(jsonPath("$.error").value("Error al crear la reservación"));
    }

     @Test
    void testCompletarReservacion_Success() throws Exception {
        // Crear un objeto Factura simulado
        Factura factura = new Factura();
        factura.setId(1L);
        factura.setMonto(100.0f);

        // Simular la respuesta del servicio
        when(reservacionService.completarReservacion(1L)).thenReturn(factura);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(post("/api/reservacion/private/completarReservacion")
                .param("idReservacion", "1"))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Convertir la respuesta a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());  // Verificar el código de estado
        assertEquals("Reservación completada y factura generada", response.getMessage());  // Verificar el mensaje
        Factura data = objectMapper.convertValue(response.getData(), Factura.class);
        assertEquals(1L, data.getId());
        assertEquals(100.0f, data.getMonto());

        // Verificar que el servicio fue llamado correctamente
        verify(reservacionService, times(1)).completarReservacion(1L);
    }

    @Test
    void testCompletarReservacion_NotFound() throws Exception {
        // Simular que el servicio lanza una EntityNotFoundException al no encontrar la reservación
        when(reservacionService.completarReservacion(1L)).thenThrow(new EntityNotFoundException("Reservación no encontrada"));

        // Realizar la solicitud y verificar el estado de la respuesta
        mockMvc.perform(post("/api/reservacion/private/completarReservacion")
                .param("idReservacion", "1"))
                .andExpect(status().isNotFound()) // Verificar que el estado de respuesta es 404 NOT FOUND
                .andExpect(jsonPath("$.message").value("Reservación no encontrada"));  // Verificar el mensaje de error

        // Verificar que el servicio fue llamado correctamente
        verify(reservacionService, times(1)).completarReservacion(1L);
    }

    @Test
    void testCompletarReservacion_InternalServerError() throws Exception {
        // Simular que el servicio lanza una excepción general para un error interno
        when(reservacionService.completarReservacion(1L)).thenThrow(new RuntimeException("Error interno del servidor"));

        // Realizar la solicitud y verificar el estado de la respuesta
        mockMvc.perform(post("/api/reservacion/private/completarReservacion")
                .param("idReservacion", "1"))
                .andExpect(status().isInternalServerError()) // Verificar que el estado de respuesta es 500 INTERNAL SERVER ERROR
                .andExpect(jsonPath("$.message").value("Error interno del servidor"));  // Verificar el mensaje de error

        // Verificar que el servicio fue llamado correctamente
        verify(reservacionService, times(1)).completarReservacion(1L);
    }
    // Helper method to convert object to JSON string
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Registro del módulo JavaTime para manejar fechas
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCancelarReservacion_Success() throws Exception {
        // Crear un DTO de cancelación simulado
        CreateCancelacionDto cancelacionRequest = new CreateCancelacionDto();
        cancelacionRequest.setIdReservacion(1L);
        cancelacionRequest.setMotivoCancelacion("Cambio de planes");
        cancelacionRequest.setFechaCancelacion(LocalDate.now());

        // Simular la respuesta del servicio
        String mensajeExito = "Reservación cancelada exitosamente y factura generada.";
        when(reservacionService.cancelarReservacion(1L, "Cambio de planes", LocalDate.now())).thenReturn(mensajeExito);

        // Realizar la solicitud y verificar la respuesta esperada
        MvcResult result = mockMvc.perform(post("/api/reservacion/public/cancelarReservacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cancelacionRequest)))
                .andExpect(status().isOk()) // Verificar que el estado de respuesta es 200 OK
                .andReturn();

        // Convertir la respuesta a un objeto JSON
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiBaseTransformer response = objectMapper.readValue(jsonResponse, ApiBaseTransformer.class);

        // Verificar los valores en el objeto JSON deserializado
        assertEquals(200, response.getCode());  // Verificar el código de estado
        assertEquals("Reservacion cancelada exitosamente y factura generada.", response.getMessage());  // Verificar el mensaje
        assertEquals(mensajeExito, response.getData());

        // Verificar que el servicio fue llamado correctamente
        verify(reservacionService, times(1)).cancelarReservacion(1L, "Cambio de planes", LocalDate.now());
    }

    @Test
    void testCancelarReservacion_InternalServerError() throws Exception {
        // Crear un DTO de cancelación simulado
        CreateCancelacionDto cancelacionRequest = new CreateCancelacionDto();
        cancelacionRequest.setIdReservacion(1L);
        cancelacionRequest.setMotivoCancelacion("Cambio de planes");
        cancelacionRequest.setFechaCancelacion(LocalDate.now());

        // Simular una excepción para el caso de error interno
        when(reservacionService.cancelarReservacion(1L, "Cambio de planes", LocalDate.now()))
                .thenThrow(new RuntimeException("Error interno del servidor"));

        // Realizar la solicitud y verificar el estado de la respuesta
        mockMvc.perform(post("/api/reservacion/public/cancelarReservacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cancelacionRequest)))
                .andExpect(status().isInternalServerError()) // Verificar que el estado de respuesta es 500 INTERNAL SERVER ERROR
                .andExpect(jsonPath("$.message").value("Error interno del servidor"));

        // Verificar que el servicio fue llamado correctamente
        verify(reservacionService, times(1)).cancelarReservacion(1L, "Cambio de planes", LocalDate.now());
    }
}
