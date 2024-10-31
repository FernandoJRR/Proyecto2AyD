package com.university.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.TipoServicio;

@Repository
public interface TipoServicioRepository extends CrudRepository<TipoServicio, Long> {
    public Optional<TipoServicio> findOneByNombre(String nombre);
}