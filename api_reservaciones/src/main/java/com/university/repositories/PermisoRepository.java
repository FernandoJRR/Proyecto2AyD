package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Permiso;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends CrudRepository<Permiso, Long> {

    @Override
    public List<Permiso> findAll();

    public Optional<Permiso> findOneByNombre(String nombre);

    public boolean existsByNombre(String nombre);
}
