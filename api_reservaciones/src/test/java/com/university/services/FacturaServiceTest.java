package com.university.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import com.university.models.Cancelacion;
import com.university.models.Factura;
import com.university.models.Reservacion;
import com.university.repositories.CancelacionRepository;
import com.university.repositories.FacturaRepository;
import com.university.repositories.ReservacionRepository;
import com.university.services.jasper.FacturaCancelacionImprimible;
import com.university.services.jasper.FacturaImprimible;
import com.university.transformers.ApiBaseTransformer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

public class FacturaServiceTest {

    @InjectMocks
    private FacturaService facturaService;

    @Mock
    private FacturaImprimible facturaImprimible;

    @Mock
    private FacturaCancelacionImprimible facturaCancelacionImprimible;

    @Mock
    private ReservacionRepository reservacionRepository;

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private CancelacionRepository cancelacionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

        @Test
    void testGetReservacion_Success() throws Exception {
        Long reservacionId = 1L;
        Reservacion reservacion = new Reservacion();
        reservacion.setId(reservacionId);

        when(reservacionRepository.findById(reservacionId)).thenReturn(Optional.of(reservacion));

        Reservacion result = facturaService.getReservacion(reservacionId);

        assertNotNull(result);
        assertEquals(reservacionId, result.getId());
        verify(reservacionRepository, times(1)).findById(reservacionId);
    }

    @Test
    void testGetReservacion_InvalidId() {
        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getReservacion(-1L);
        });

        assertEquals("Id invalido.", exception.getMessage());
    }

    @Test
    void testGetReservacion_NotFound() {
        Long reservacionId = 1L;

        when(reservacionRepository.findById(reservacionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getReservacion(reservacionId);
        });

        assertEquals("Reservacion no encontrada.", exception.getMessage());
    }

    @Test
    void testGetFacturaByReservacionId_Success() throws Exception {
        Long reservacionId = 1L;
        Factura factura = new Factura();

        when(facturaRepository.findByReservacionId(reservacionId)).thenReturn(Optional.of(factura));

        Factura result = facturaService.getFacturaByReservacionId(reservacionId);

        assertNotNull(result);
        verify(facturaRepository, times(1)).findByReservacionId(reservacionId);
    }

    @Test
    void testGetFacturaByReservacionId_InvalidId() {
        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getFacturaByReservacionId(-1L);
        });

        assertEquals("Id invalido.", exception.getMessage());
    }

    @Test
    void testGetFacturaByReservacionId_NotFound() {
        Long reservacionId = 1L;

        when(facturaRepository.findByReservacionId(reservacionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getFacturaByReservacionId(reservacionId);
        });

        assertEquals("Factura no encontrada.", exception.getMessage());
    }

    @Test
    void testGetCancelacionByReservacionId_Success() throws Exception {
        Long reservacionId = 1L;
        Cancelacion cancelacion = new Cancelacion();

        when(cancelacionRepository.findByReservacionId(reservacionId)).thenReturn(Optional.of(cancelacion));

        Cancelacion result = facturaService.getCancelacionByReservacionId(reservacionId);

        assertNotNull(result);
        verify(cancelacionRepository, times(1)).findByReservacionId(reservacionId);
    }

    @Test
    void testGetCancelacionByReservacionId_InvalidId() {
        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getCancelacionByReservacionId(-1L);
        });

        assertEquals("Id invalido.", exception.getMessage());
    }

    @Test
    void testGetCancelacionByReservacionId_NotFound() {
        Long reservacionId = 1L;

        when(cancelacionRepository.findByReservacionId(reservacionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getCancelacionByReservacionId(reservacionId);
        });

        assertEquals("Cancelacion no encontrada.", exception.getMessage());
    }

    @Test
    void testGetFactura_Success() throws Exception {
        Long reservacionId = 1L;
        Reservacion reservacion = new Reservacion();
        Factura factura = new Factura();
        byte[] expectedBytes = new byte[]{1, 2, 3};

        when(reservacionRepository.findById(reservacionId)).thenReturn(Optional.of(reservacion));
        when(facturaRepository.findByReservacionId(reservacionId)).thenReturn(Optional.of(factura));
        when(facturaImprimible.init(reservacion, factura)).thenReturn(expectedBytes);

        byte[] result = facturaService.getFactura(reservacionId);

        assertArrayEquals(expectedBytes, result);
        verify(reservacionRepository, times(1)).findById(reservacionId);
        verify(facturaRepository, times(1)).findByReservacionId(reservacionId);
        verify(facturaImprimible, times(1)).init(reservacion, factura);
    }

    @Test
    void testGetFactura_FacturaNotFound() throws Exception {
        Long reservacionId = 1L;
        Reservacion reservacion = new Reservacion();

        when(reservacionRepository.findById(reservacionId)).thenReturn(Optional.of(reservacion));
        when(facturaRepository.findByReservacionId(reservacionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getFactura(reservacionId);
        });

        assertEquals("Factura no encontrada.", exception.getMessage());
        verify(reservacionRepository, times(1)).findById(reservacionId);
        verify(facturaRepository, times(1)).findByReservacionId(reservacionId);
    }

    @Test
    void testGetFacturaCancelacion_Success() throws Exception {
        Long reservacionId = 1L;
        Reservacion reservacion = new Reservacion();
        Factura factura = new Factura();
        Cancelacion cancelacion = new Cancelacion();
        byte[] expectedBytes = new byte[]{1, 2, 3};

        when(reservacionRepository.findById(reservacionId)).thenReturn(Optional.of(reservacion));
        when(facturaRepository.findByReservacionId(reservacionId)).thenReturn(Optional.of(factura));
        when(cancelacionRepository.findByReservacionId(reservacionId)).thenReturn(Optional.of(cancelacion));
        when(facturaCancelacionImprimible.init(reservacion, factura, cancelacion)).thenReturn(expectedBytes);

        byte[] result = facturaService.getFacturaCancelacion(reservacionId);

        assertArrayEquals(expectedBytes, result);
        verify(reservacionRepository, times(1)).findById(reservacionId);
        verify(facturaRepository, times(1)).findByReservacionId(reservacionId);
        verify(cancelacionRepository, times(1)).findByReservacionId(reservacionId);
        verify(facturaCancelacionImprimible, times(1)).init(reservacion, factura, cancelacion);
    }

    @Test
    void testGetFacturaCancelacion_CancelacionNotFound() throws Exception {
        Long reservacionId = 1L;
        Reservacion reservacion = new Reservacion();
        Factura factura = new Factura();

        when(reservacionRepository.findById(reservacionId)).thenReturn(Optional.of(reservacion));
        when(facturaRepository.findByReservacionId(reservacionId)).thenReturn(Optional.of(factura));
        when(cancelacionRepository.findByReservacionId(reservacionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            facturaService.getFacturaCancelacion(reservacionId);
        });

        assertEquals("Cancelacion no encontrada.", exception.getMessage());
        verify(reservacionRepository, times(1)).findById(reservacionId);
        verify(facturaRepository, times(1)).findByReservacionId(reservacionId);
        verify(cancelacionRepository, times(1)).findByReservacionId(reservacionId);
    }
}
