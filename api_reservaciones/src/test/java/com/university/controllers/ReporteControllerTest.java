package com.university.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.university.services.ReporteService;
import com.university.transformers.ApiBaseTransformer;

@SpringBootTest
@AutoConfigureMockMvc
public class ReporteControllerTest {
    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private ReporteController reporteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reporteController).build();
    }

    @Test
    void testObtenerTopServicios_Success() throws Exception {
        // Definir las fechas de prueba
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);

        // Crear datos de prueba simulados
        List<Map.Entry<String, Long>> topServicios = new ArrayList<>();
        topServicios.add(new AbstractMap.SimpleEntry<>("Servicio A", 100L));
        topServicios.add(new AbstractMap.SimpleEntry<>("Servicio B", 80L));
        topServicios.add(new AbstractMap.SimpleEntry<>("Servicio C", 60L));
        topServicios.add(new AbstractMap.SimpleEntry<>("Servicio D", 40L));
        topServicios.add(new AbstractMap.SimpleEntry<>("Servicio E", 20L));

        // Configurar el servicio simulado
        when(reporteService.topServiciosSolicitados(fechaInicio, fechaFin)).thenReturn(topServicios);

        // Llamar al método del controlador
        ResponseEntity<?> response = reporteController.obtenerTopServicios(fechaInicio, fechaFin);

        // Verificar la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiBaseTransformer transformer = (ApiBaseTransformer) response.getBody();
        assertNotNull(transformer);
        assertEquals("Reporte generado exitosamente", transformer.getMessage());
        assertEquals(topServicios, transformer.getData());

        verify(reporteService, times(1)).topServiciosSolicitados(fechaInicio, fechaFin);
    }

    @Test
    void testObtenerTopServicios_BadRequest() throws Exception {
        // Configurar fechas inválidas para generar un error
        LocalDate fechaInicio = LocalDate.of(2023, 12, 31);
        LocalDate fechaFin = LocalDate.of(2023, 1, 1);

        // Simular que el servicio lanza una excepción debido a fechas inválidas
        when(reporteService.topServiciosSolicitados(fechaInicio, fechaFin)).thenThrow(new IllegalArgumentException("Fechas no válidas"));

        // Realizar la solicitud al endpoint
        mockMvc.perform(get("/api/reporte/public/topServicios")
                .param("fechaInicio", fechaInicio.toString())
                .param("fechaFin", fechaFin.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testObtenerTopServicios_ServerError() throws Exception {
        // Definir las fechas de prueba
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);

        // Simular que el servicio lanza una excepción general
        when(reporteService.topServiciosSolicitados(fechaInicio, fechaFin)).thenThrow(new RuntimeException("Error interno del servidor"));

        // Llamar al método del controlador
        ResponseEntity<?> response = reporteController.obtenerTopServicios(fechaInicio, fechaFin);

        // Verificar que la respuesta tiene un estado 500
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiBaseTransformer transformer = (ApiBaseTransformer) response.getBody();
        assertNotNull(transformer);
        assertEquals("Error", transformer.getMessage());
        assertNull(transformer.getData());
        assertEquals("Error interno del servidor", transformer.getError());

        verify(reporteService, times(1)).topServiciosSolicitados(fechaInicio, fechaFin);
    }
}
