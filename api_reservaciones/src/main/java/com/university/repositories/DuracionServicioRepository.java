package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.DuracionServicio;

@Repository
public interface DuracionServicioRepository extends CrudRepository<DuracionServicio, Long> {

}
