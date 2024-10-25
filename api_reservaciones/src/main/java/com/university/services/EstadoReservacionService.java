package com.university.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.EstadoReservacion;
import com.university.repositories.EstadoReservacionRepository;

@Service
public class EstadoReservacionService {

    @Autowired
    private EstadoReservacionRepository estadoReservacionRepository;

    public List<EstadoReservacion> getEstadosReservacion() {
        return (List<EstadoReservacion>) estadoReservacionRepository.findAll();
    }

    public EstadoReservacion getEstadoReservacion(Long id) throws Exception {
        EstadoReservacion estadoReservacion = this.estadoReservacionRepository.findById(id).orElse(null);
        // si el rol no existe lanzamos error
        if (estadoReservacion == null) {
            throw new Exception("Estado de reservacion no encontrado.");
        }
        return estadoReservacion;
    }
}
