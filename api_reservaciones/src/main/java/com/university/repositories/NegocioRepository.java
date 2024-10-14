package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Negocio;

import java.util.List;

@Repository
public interface NegocioRepository extends CrudRepository<Negocio, Long>{

    @Override
    public List<Negocio> findAll();
}
