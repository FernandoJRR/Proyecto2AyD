package com.university.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Rol;
import com.university.repositories.RolRepository;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> getRoles() {
        return (List<Rol>) rolRepository.findAll();
    }

    public Rol getRol(String rolStr) throws Exception {
        Rol rol = this.rolRepository.findOneByNombre(rolStr).orElse(null);
        // si el rol no existe lanzamos error
        if (rol == null) {
            throw new Exception("Rol no encontrado.");
        }
        return rol;
    }

    public Rol createRol(Rol producto) {
        return rolRepository.save(producto);
    }

    public Rol updateRol(Long id, Rol producto) {
        Rol rolExistente = rolRepository.findById(id).orElse(null);
        if (rolExistente != null) {
            return rolRepository.save(rolExistente);
        }
        return null;
    }
}
