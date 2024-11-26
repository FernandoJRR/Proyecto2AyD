package com.university.services;

import org.apache.commons.beanutils.converters.LongArrayConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Negocio;
import com.university.repositories.NegocioRepository;

import java.util.List;

import javax.transaction.Transactional;

@Service
public class NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    public Negocio createNegocio(Negocio negocio) {
        return this.negocioRepository.save(negocio);
    }

    public Negocio getNegocio(Long id) throws Exception {
        Negocio negocio = this.negocioRepository.findById(id).orElse(null);
        if (negocio == null) {
            throw new Exception("Negocio no encontrado.");
        }
        return negocio;
    }

    public List<Negocio> getNegocios() {
        return this.negocioRepository.findAll();
    }

    @Transactional
    public Negocio updateNegocio(Negocio update) throws Exception {
        // Busca el negocio por ID
        Negocio negocio = this.negocioRepository.findById(update.getId())
                .orElseThrow(() -> new Exception("Negocio no encontrado."));

        // Actualiza los campos necesarios
        if (update.getNombre() != null && !update.getNombre().isBlank()) {
            negocio.setNombre(update.getNombre());
        }

        // Guarda y retorna el negocio actualizado
        return this.negocioRepository.save(negocio);
    }
}
