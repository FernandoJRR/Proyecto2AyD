package com.university.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import com.university.repositories.ReservacionRepository;
import com.university.transformers.ApiBaseTransformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

public class ReporteServiceTest {


    @InjectMocks
    private ReporteService reporteService;

    @Mock
    private ReservacionRepository reservacionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerarReporteReservaciones_SuccessWithEstado() throws Exception {
        LocalDate fechaInicio = LocalDate.of(2023, 10, 1);
        LocalDate fechaFin = LocalDate.of(2023, 10, 31);
        String estado = "Completa";

        when(reservacionRepository.countByFechaAndEstado(fechaInicio, fechaFin, estado)).thenReturn(5L);

        Long result = reporteService.generarReporteReservaciones(fechaInicio, fechaFin, estado);

        assertEquals(5L, result);
    }

    @Test
    void testGenerarReporteReservaciones_SuccessWithoutEstado() throws Exception {
        LocalDate fechaInicio = LocalDate.of(2023, 10, 1);
        LocalDate fechaFin = LocalDate.of(2023, 10, 31);

        when(reservacionRepository.countByFechaAndEstado(fechaInicio, fechaFin, null)).thenReturn(15L);

        Long result = reporteService.generarReporteReservaciones(fechaInicio, fechaFin, null);

        assertEquals(15L, result);
    }

    @Test
    void testTopServiciosSolicitados_Success() {
        // Definir fechas de prueba
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);

        // Crear resultados de prueba simulados para el repositorio
        List<Object[]> resultados = Arrays.asList(
                new Object[]{"Servicio A", 100L},
                new Object[]{"Servicio B", 80L},
                new Object[]{"Servicio C", 60L},
                new Object[]{"Servicio D", 40L},
                new Object[]{"Servicio E", 20L},
                new Object[]{"Servicio F", 10L}
        );

        // Configurar el repositorio para devolver estos resultados
        when(reservacionRepository.contarServiciosPorFecha(fechaInicio, fechaFin)).thenReturn(resultados);

        // Llamar al método del servicio
        List<Map.Entry<String, Long>> topServicios = reporteService.topServiciosSolicitados(fechaInicio, fechaFin);

        // Verificar que el top 5 se obtenga correctamente
        assertEquals(5, topServicios.size());
        assertEquals("Servicio A", topServicios.get(0).getKey());
        assertEquals(100L, topServicios.get(0).getValue());
        assertEquals("Servicio B", topServicios.get(1).getKey());
        assertEquals(80L, topServicios.get(1).getValue());

        verify(reservacionRepository, times(1)).contarServiciosPorFecha(fechaInicio, fechaFin);
    }

    @Test
    void testTopServiciosSolicitados_MenosDeCincoServicios() {
        // Definir fechas de prueba
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);

        // Simular que solo hay tres servicios en el resultado
        List<Object[]> resultados = Arrays.asList(
                new Object[]{"Servicio A", 100L},
                new Object[]{"Servicio B", 80L},
                new Object[]{"Servicio C", 60L}
        );

        when(reservacionRepository.contarServiciosPorFecha(fechaInicio, fechaFin)).thenReturn(resultados);

        List<Map.Entry<String, Long>> topServicios = reporteService.topServiciosSolicitados(fechaInicio, fechaFin);

        // Verificar que la lista contiene todos los servicios disponibles
        assertEquals(3, topServicios.size());
        assertEquals("Servicio A", topServicios.get(0).getKey());
        assertEquals(100L, topServicios.get(0).getValue());
        assertEquals("Servicio B", topServicios.get(1).getKey());
        assertEquals(80L, topServicios.get(1).getValue());

        verify(reservacionRepository, times(1)).contarServiciosPorFecha(fechaInicio, fechaFin);
    }

    @Test
    void testTopServiciosSolicitados_SinServicios() {
        // Definir fechas de prueba
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);

        // Simular que no hay servicios en el resultado
        when(reservacionRepository.contarServiciosPorFecha(fechaInicio, fechaFin)).thenReturn(Collections.emptyList());

        List<Map.Entry<String, Long>> topServicios = reporteService.topServiciosSolicitados(fechaInicio, fechaFin);

        // Verificar que la lista esté vacía
        assertTrue(topServicios.isEmpty());
        verify(reservacionRepository, times(1)).contarServiciosPorFecha(fechaInicio, fechaFin);
    }

    @Test
    void testTopServiciosSolicitados_FechaInvalida() {
        // Definir fechas inválidas (inicio después de fin)
        LocalDate fechaInicio = LocalDate.of(2023, 12, 31);
        LocalDate fechaFin = LocalDate.of(2023, 1, 1);

        // Verificar que una excepción se lanza al usar fechas no válidas
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reporteService.topServiciosSolicitados(fechaInicio, fechaFin);
        });

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());
        verify(reservacionRepository, times(0)).contarServiciosPorFecha(fechaInicio, fechaFin);
    }

}
