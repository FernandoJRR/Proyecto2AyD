package com.university.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.EstadoReservacion;
import com.university.models.MetodoPago;

@Repository
public interface EstadoReservacionRepository extends CrudRepository<EstadoReservacion, Long> {

    @Override
    public List<EstadoReservacion> findAll();

    public Optional<EstadoReservacion> findOneByNombre(String nombre);
}
