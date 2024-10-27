package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Factura;

@Repository
public interface FacturaRepository extends CrudRepository<Factura, Long> {

}
