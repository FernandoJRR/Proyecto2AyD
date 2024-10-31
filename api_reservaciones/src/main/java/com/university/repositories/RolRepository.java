package com.university.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Rol;

@Repository
public interface RolRepository extends CrudRepository<Rol, Long> {
    public Optional<Rol> findOneByNombre(String nombre);
}
