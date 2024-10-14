package com.university.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.university.models.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

    //Busqueda de usuario por email
    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByCodigoRecuperacion(String codigo);

    public Long deleteUsuarioById(Long id);

    public boolean existsByEmail(String email);

    public boolean existsByNit(String nit);

    public boolean existsUsuarioByEmailAndIdNot(String email, Long id);

    public boolean existsUsuarioByNitAndIdNot(String nit, Long id);

    @Override
    public List<Usuario> findAll();
}
