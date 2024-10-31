package com.university.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Recurso;

@Repository
public interface RecursoRepository extends CrudRepository<Recurso, Long> {
    public Optional<Recurso> findOneByNombre(String nombre);
}
