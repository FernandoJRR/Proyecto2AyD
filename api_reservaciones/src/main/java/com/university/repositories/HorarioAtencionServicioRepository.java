package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.HorarioAtencionServicio;

@Repository
public interface HorarioAtencionServicioRepository extends CrudRepository<HorarioAtencionServicio, Long> {

}
