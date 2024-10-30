package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.repositories.ReservacionRepository;

import java.time.LocalDate;

@Service
public class ReporteService {

    @Autowired
    private ReservacionRepository reservacionRepository;

    public Long generarReporteReservaciones(LocalDate fechaInicio, LocalDate fechaFin, String estado) throws Exception {
        if (fechaInicio == null || fechaFin == null) {
            throw new Exception("Las fechas de inicio y finalización son obligatorias.");
        }

        return reservacionRepository.countByFechaAndEstado(fechaInicio, fechaFin, estado);
    }
}
