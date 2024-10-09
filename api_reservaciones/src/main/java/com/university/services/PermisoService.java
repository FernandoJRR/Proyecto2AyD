package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Permiso;
import com.university.repositories.PermisoRepository;

import java.util.List;

@Service
public class PermisoService {

    @Autowired
    private PermisoRepository permisoRepository;

    /**
     * Devuelve todas los permisos
     *
     * @return
     */
    public List<Permiso> getPermisos() {
        return this.permisoRepository.findAll();
    }
}
