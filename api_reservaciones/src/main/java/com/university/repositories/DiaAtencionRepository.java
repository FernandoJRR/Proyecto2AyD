package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.DiaAtencion;

import java.util.Optional;
import java.util.List;

@Repository
public interface DiaAtencionRepository extends CrudRepository<DiaAtencion, Long> {

    @Override
    public List<DiaAtencion> findAll();

    public Optional<DiaAtencion> findOneByNombre(String nombre);
}
