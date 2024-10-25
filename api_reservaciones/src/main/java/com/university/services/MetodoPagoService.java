package com.university.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.models.MetodoPago;
import com.university.repositories.MetodoPagoRepository;

@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    public List<MetodoPago> getMetodosPago() {
        return (List<MetodoPago>) metodoPagoRepository.findAll();
    }

    public MetodoPago getMetodoPago(Long id) throws Exception {
        MetodoPago metodoPago = this.metodoPagoRepository.findById(id).orElse(null);
        // si el rol no existe lanzamos error
        if (metodoPago == null) {
            throw new Exception("Metodo de pago no encontrado.");
        }
        return metodoPago;
    }
}
