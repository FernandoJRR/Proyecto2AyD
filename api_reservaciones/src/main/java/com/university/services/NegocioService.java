package com.university.services;

import org.apache.commons.beanutils.converters.LongArrayConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.Negocio;
import com.university.repositories.NegocioRepository;

import java.util.List;

@Service
public class NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    public Negocio createNegocio(Negocio negocio) {
        return negocioRepository.save(negocio);
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
}
