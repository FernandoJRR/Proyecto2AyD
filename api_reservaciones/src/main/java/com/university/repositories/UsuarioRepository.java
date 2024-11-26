package com.university.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.models.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    // Busqueda de usuario por email
    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByCodigoRecuperacion(String codigo);

    public Optional<Usuario> findByCodigoVerificacion(String codigo);

    public Long deleteUsuarioById(Long id);

    public boolean existsByEmail(String email);

    public boolean existsByNit(String nit);

    public boolean existsUsuarioByEmailAndIdNot(String email, Long id);

    public boolean existsUsuarioByNitAndIdNot(String nit, Long id);

    @Override
    public List<Usuario> findAll();

    @Query("SELECT u FROM Usuario u " +
            "JOIN u.roles ur " +
            "JOIN ur.rol r " +
            "JOIN r.servicios sr " +
            "JOIN sr.servicio s " +
            "WHERE s.id = :servicioId")
    List<Usuario> findUsuariosConPermisosParaServicio(@Param("servicioId") Long servicioId);

            @Query("SELECT DISTINCT ur.usuario FROM UsuarioRol ur " +
            "JOIN ur.rol r " +
            "JOIN r.servicios sr " +
            "WHERE sr.servicio.id = :servicioId")
    List<Usuario> findUsuariosByServicioId(@Param("servicioId") Long servicioId);

}
