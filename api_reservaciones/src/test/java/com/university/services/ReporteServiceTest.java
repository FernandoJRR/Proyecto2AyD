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
    @Test
    void testContarReservacionesPorTiempoYCantidad_SemanaConEstado() throws Exception {
        String tipoTiempo = "semana";
        int cantidad = 2;
        String estado = "Completa";

        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusWeeks(cantidad);

        when(reservacionRepository.countByFechaBetweenAndEstadoReservacion_Nombre(fechaInicio, fechaFin, estado)).thenReturn(10L);

        long result = reporteService.contarReservacionesPorTiempoYCantidad(tipoTiempo, cantidad, estado);
        assertEquals(10L, result);
    }

    @Test
    void testContarReservacionesPorTiempoYCantidad_MesSinEstado() throws Exception {
        String tipoTiempo = "mes";
        int cantidad = 3;

        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusMonths(cantidad);

        when(reservacionRepository.countByFechaBetween(fechaInicio, fechaFin)).thenReturn(20L);

        long result = reporteService.contarReservacionesPorTiempoYCantidad(tipoTiempo, cantidad, null);
        assertEquals(20L, result);
    }

    @Test
    void testContarReservacionesPorTiempoYCantidad_AnioConEstado() throws Exception {
        String tipoTiempo = "anio";
        int cantidad = 1;
        String estado = "Pendiente";

        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusYears(cantidad);

        when(reservacionRepository.countByFechaBetweenAndEstadoReservacion_Nombre(fechaInicio, fechaFin, estado)).thenReturn(5L);

        long result = reporteService.contarReservacionesPorTiempoYCantidad(tipoTiempo, cantidad, estado);
        assertEquals(5L, result);
    }

    @Test
    void testContarReservacionesPorTiempoYCantidad_TipoTiempoInvalido() {
        String tipoTiempo = "dia";
        int cantidad = 5;
        String estado = "Completa";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reporteService.contarReservacionesPorTiempoYCantidad(tipoTiempo, cantidad, estado);
        });

        assertEquals("Tipo de tiempo no válido. Usa 'semana', 'mes' o 'año'.", exception.getMessage());
    }

    @Test
    void testContarReservacionesPorTiempoYCantidad_SemanaSinEstado() throws Exception {
        String tipoTiempo = "semana";
        int cantidad = 4;

        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusWeeks(cantidad);

        when(reservacionRepository.countByFechaBetween(fechaInicio, fechaFin)).thenReturn(15L);

        long result = reporteService.contarReservacionesPorTiempoYCantidad(tipoTiempo, cantidad, null);
        assertEquals(15L, result);
    }

    @Test
    void testContarReservacionesPorTiempoYCantidad_MesConEstado() throws Exception {
        String tipoTiempo = "mes";
        int cantidad = 2;
        String estado = "Cancelada";

        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusMonths(cantidad);

        when(reservacionRepository.countByFechaBetweenAndEstadoReservacion_Nombre(fechaInicio, fechaFin, estado)).thenReturn(8L);

        long result = reporteService.contarReservacionesPorTiempoYCantidad(tipoTiempo, cantidad, estado);
        assertEquals(8L, result);
    }

    @Test
    void testObtenerServiciosMasReservados_Success() {
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);

        List<Object[]> resultados = Arrays.asList(
                new Object[]{"Servicio A", 100L},
                new Object[]{"Servicio B", 80L},
                new Object[]{"Servicio C", 60L}
        );

        when(reservacionRepository.contarReservasPorServicio(fechaInicio, fechaFin)).thenReturn(resultados);

        List<Map<String, Object>> reporte = reporteService.obtenerServiciosMasReservados(fechaInicio, fechaFin);

        assertEquals(3, reporte.size());
        assertEquals("Servicio A", reporte.get(0).get("nombre"));
        assertEquals(100L, reporte.get(0).get("totalReservas"));
        assertEquals("Servicio B", reporte.get(1).get("nombre"));
        assertEquals(80L, reporte.get(1).get("totalReservas"));

        verify(reservacionRepository, times(1)).contarReservasPorServicio(fechaInicio, fechaFin);
    }

    @Test
    void testObtenerServiciosMasReservados_SinResultados() {
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);

        when(reservacionRepository.contarReservasPorServicio(fechaInicio, fechaFin)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> reporte = reporteService.obtenerServiciosMasReservados(fechaInicio, fechaFin);

        assertTrue(reporte.isEmpty());
        verify(reservacionRepository, times(1)).contarReservasPorServicio(fechaInicio, fechaFin);
    }

    @Test
    void testObtenerTopClientesConMasReservaciones_Success() {
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);
        int topN = 2;

        List<Object[]> resultados = Arrays.asList(
                new Object[]{1L, 50L},
                new Object[]{2L, 40L},
                new Object[]{3L, 30L}
        );

        when(reservacionRepository.contarReservacionesPorCliente(fechaInicio, fechaFin)).thenReturn(resultados);

        List<Map<String, Object>> reporte = reporteService.obtenerTopClientesConMasReservaciones(fechaInicio, fechaFin, topN);

        assertEquals(2, reporte.size());
        assertEquals(1L, reporte.get(0).get("clienteId"));
        assertEquals(50L, reporte.get(0).get("cantidadReservaciones"));
        assertEquals(2L, reporte.get(1).get("clienteId"));
        assertEquals(40L, reporte.get(1).get("cantidadReservaciones"));

        verify(reservacionRepository, times(1)).contarReservacionesPorCliente(fechaInicio, fechaFin);
    }

    @Test
    void testObtenerTopClientesConMasReservaciones_SinResultados() {
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);
        int topN = 2;

        when(reservacionRepository.contarReservacionesPorCliente(fechaInicio, fechaFin)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> reporte = reporteService.obtenerTopClientesConMasReservaciones(fechaInicio, fechaFin, topN);

        assertTrue(reporte.isEmpty());
        verify(reservacionRepository, times(1)).contarReservacionesPorCliente(fechaInicio, fechaFin);
    }

    @Test
    void testObtenerTopClientesConMasReservaciones_TopMayorQueResultados() {
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);
        int topN = 5;

        List<Object[]> resultados = Arrays.asList(
                new Object[]{1L, 50L},
                new Object[]{2L, 40L}
        );

        when(reservacionRepository.contarReservacionesPorCliente(fechaInicio, fechaFin)).thenReturn(resultados);

        List<Map<String, Object>> reporte = reporteService.obtenerTopClientesConMasReservaciones(fechaInicio, fechaFin, topN);

        assertEquals(2, reporte.size());
        assertEquals(1L, reporte.get(0).get("clienteId"));
        assertEquals(50L, reporte.get(0).get("cantidadReservaciones"));
        assertEquals(2L, reporte.get(1).get("clienteId"));
        assertEquals(40L, reporte.get(1).get("cantidadReservaciones"));

        verify(reservacionRepository, times(1)).contarReservacionesPorCliente(fechaInicio, fechaFin);
    }
}
