package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Recurso;
import com.university.models.UnidadRecurso;
import com.university.repositories.RecursoRepository;
import com.university.repositories.UnidadRecursoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadRecursoService {

    @Autowired
    private UnidadRecursoRepository unidadRecursoRepository;
    @Autowired
    private RecursoRepository recursoRepository;

    public UnidadRecurso createUnidadRecurso(UnidadRecurso unidadRecurso) {
        Optional<Recurso> recursoUnidad = recursoRepository.findById(unidadRecurso.getRecurso().getId());
        if (recursoUnidad.isEmpty()) {
            throw new IllegalArgumentException("El Recurso no existe");
        }
        return this.unidadRecursoRepository.save(unidadRecurso);
    }

    public UnidadRecurso getUnidadRecurso(Long id) throws Exception {
        UnidadRecurso unidadRecurso = this.unidadRecursoRepository.findById(id).orElse(null);
        if (unidadRecurso == null) {
            throw new Exception("Unidad de Recurso no encontrada.");
        }
        return unidadRecurso;
    }

    public List<UnidadRecurso> getUnidadRecursoByRecursoId(Long id_recurso) {
        Recurso recurso = new Recurso(id_recurso);
        return this.unidadRecursoRepository.findByRecurso(recurso);
    }

    public List<UnidadRecurso> getUnidadRecurso() {
        return this.unidadRecursoRepository.findAll();
    }
}
