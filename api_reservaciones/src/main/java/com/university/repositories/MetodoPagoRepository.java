package com.university.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.MetodoPago;

@Repository
public interface MetodoPagoRepository extends CrudRepository<MetodoPago, Long> {

    @Override
    public List<MetodoPago> findAll();

    public Optional<MetodoPago> findOneByNombre(String nombre);
}
