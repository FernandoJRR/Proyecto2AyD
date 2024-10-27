package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Cancelacion;

@Repository
public interface CancelacionRepository extends CrudRepository<Cancelacion, Long> {

}
