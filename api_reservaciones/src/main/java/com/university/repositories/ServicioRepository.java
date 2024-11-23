package com.university.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.models.Servicio;
import com.university.models.UnidadRecurso;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends CrudRepository<Servicio, Long>{

    public Optional<Servicio> findOneByNombre(String nombre);

    public List<Servicio> findByNegocioId(Long negocioId);

   @Query("""
        SELECT ur
        FROM UnidadRecurso ur
        WHERE ur.recurso.id =
              (SELECT s.recurso.id
               FROM Servicio s
               WHERE s.id = :servicioId
               AND s.recurso IS NOT NULL)
    """)
    List<UnidadRecurso> findUnidadesRecursoByServicioId(@Param("servicioId") Long servicioId);
}
