package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Pago;

@Repository
public interface PagoRepository extends CrudRepository<Pago, Long> {

}
