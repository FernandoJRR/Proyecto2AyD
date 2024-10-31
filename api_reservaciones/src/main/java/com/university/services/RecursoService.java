package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Recurso;
import com.university.models.Servicio;
import com.university.repositories.RecursoRepository;

import java.util.List;

@Service
public class RecursoService {
    @Autowired
    private RecursoRepository recursoRepository;

    public List<Recurso> getRecursos() {
        return (List<Recurso>) recursoRepository.findAll();
    }

    public Recurso getRecurso(Long id) throws Exception {
        Recurso recurso = this.recursoRepository.findById(id).orElse(null);
        if (recurso == null) {
            throw new Exception("Recurso no encontrado.");
        }
        return recurso;
    }

    public Recurso createRecurso(Recurso recurso) throws Exception {
        return this.recursoRepository.save(recurso);
    }
}
