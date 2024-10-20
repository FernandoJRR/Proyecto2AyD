package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.DuracionServicio;
import com.university.models.Servicio;
import com.university.models.request.CreateServicioDto;
import com.university.repositories.DuracionServicioRepository;
import com.university.repositories.ServicioRepository;

import java.util.List;

import javax.transaction.Transactional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private DuracionServicioRepository duracionServicioRepository;

    public List<Servicio> getServicios() {
        return (List<Servicio>) servicioRepository.findAll();
    }

    public Servicio getServicio(Long id) throws Exception {
        Servicio servicio = servicioRepository.findById(id).orElse(null);
        // si el rol no existe lanzamos error
        if (servicio == null) {
            throw new Exception("Servicio no encontrado.");
        }
        return servicio;
    }

    @Transactional
    public Servicio createServicio(CreateServicioDto servicioDto) throws Exception {
        Servicio servicio = servicioDto.getServicio();
        DuracionServicio duracionServicio = servicioDto.getDuracionServicio();

        if (duracionServicio.equals(null)) {
            throw new Exception("Debes asignar una duracion al servicio");
        }

        Servicio servicioCreado = servicioRepository.save(servicio);

        //Se le asigna una duracion al Servicio
        DuracionServicio duracionServicioCreado = new DuracionServicio(
            servicioCreado, duracionServicio.getMinutos(), duracionServicio.getHoras()
        );

        duracionServicioRepository.save(duracionServicioCreado);

        return servicioCreado;
    }

    public Servicio updateRol(Long id, Servicio servicio) {
        Servicio servicioExistente = servicioRepository.findById(id).orElse(null);
        if (servicioExistente != null) {
            return servicioRepository.save(servicioExistente);
        }
        return null;
    }
}
