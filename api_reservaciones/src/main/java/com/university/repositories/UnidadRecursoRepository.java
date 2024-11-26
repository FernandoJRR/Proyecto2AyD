package com.university.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Recurso;
import com.university.models.UnidadRecurso;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadRecursoRepository extends CrudRepository<UnidadRecurso, Long> {

    @Override
    public List<UnidadRecurso> findAll();

    List<UnidadRecurso> findByRecurso(Recurso recurso);

    Optional<UnidadRecurso> findOneByNombre(String nombre);

    Optional<UnidadRecurso> findByNombreAndRecurso_Nombre(String unidadRecursoNombre, String recursoNombre);
}
