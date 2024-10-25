package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.EstadoReservacion;
import com.university.models.MetodoPago;

@Repository
public interface EstadoReservacionRepository extends CrudRepository<EstadoReservacion, Long> {

}
