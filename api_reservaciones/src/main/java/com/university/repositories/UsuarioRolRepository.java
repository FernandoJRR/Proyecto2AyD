package com.university.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.models.Usuario;
import com.university.models.UsuarioRol;

@Repository
public interface UsuarioRolRepository extends CrudRepository<UsuarioRol, Long>{
  @Query("SELECT ur.usuario FROM UsuarioRol ur WHERE ur.rol.nombre = :rolNombre")
    List<Usuario> findUsuariosByRolNombre(@Param("rolNombre") String rolNombre);
}
