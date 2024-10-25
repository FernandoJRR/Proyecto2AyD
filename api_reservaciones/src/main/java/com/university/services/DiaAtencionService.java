package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.DiaAtencion;
import com.university.repositories.DiaAtencionRepository;

import java.util.List;

@Service
public class DiaAtencionService {

    @Autowired
    private DiaAtencionRepository diaAtencionRepository;

    /**
     * Devuelve todas los dias atencion
     *
     * @return
     */
    public List<DiaAtencion> getDiasAntecion() {
        return this.diaAtencionRepository.findAll();
    }

}
