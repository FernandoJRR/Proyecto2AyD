package com.university.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Factura;

@Repository
public interface FacturaRepository extends CrudRepository<Factura, Long> {
    Optional<Factura> findByReservacionId(Long reservacionId);
}
