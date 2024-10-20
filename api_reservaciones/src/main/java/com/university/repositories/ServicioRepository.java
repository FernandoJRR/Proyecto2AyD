package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Servicio;

@Repository
public interface ServicioRepository extends CrudRepository<Servicio, Long>{
}