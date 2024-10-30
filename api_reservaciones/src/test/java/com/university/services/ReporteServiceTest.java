package com.university.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.university.repositories.ReservacionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

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
}
