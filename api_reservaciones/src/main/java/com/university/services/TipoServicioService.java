package com.university.services;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Permiso;
import com.university.models.PermisoRol;
import com.university.models.Rol;
import com.university.models.Servicio;
import com.university.models.ServicioRol;
import com.university.models.TipoServicio;
import com.university.models.request.CreateRolDto;
import com.university.models.request.PermisoRolRequest;
import com.university.repositories.RolRepository;
import com.university.repositories.TipoServicioRepository;

@Service
public class TipoServicioService {

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    public List<TipoServicio> getTiposServicio() {
        return (List<TipoServicio>) tipoServicioRepository.findAll();
    }

    public TipoServicio getTipoServicio(Long id) throws Exception {
        TipoServicio tipoServicio = this.tipoServicioRepository.findById(id).orElse(null);
        // si el rol no existe lanzamos error
        if (tipoServicio == null) {
            throw new Exception("Tipo de Servicio no encontrado.");
        }
        return tipoServicio;
    }

}
