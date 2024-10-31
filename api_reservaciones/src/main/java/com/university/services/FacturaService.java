package com.university.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.enums.EstadoReservacionEnum;
import com.university.models.Cancelacion;
import com.university.models.Factura;
import com.university.models.Reservacion;
import com.university.repositories.CancelacionRepository;
import com.university.repositories.FacturaRepository;
import com.university.repositories.ReservacionRepository;
import com.university.services.jasper.FacturaCancelacionImprimible;
import com.university.services.jasper.FacturaImprimible;

@Service
public class FacturaService {

    @Autowired
    private FacturaImprimible facturaImprimible;

    @Autowired
    private FacturaCancelacionImprimible facturaCancelacionImprimible;

    @Autowired
    private ReservacionRepository reservacionRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private CancelacionRepository cancelacionRepository;

    public Reservacion getReservacion(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Id invalido.");
        }

        Optional<Reservacion> reservacionSearch = this.reservacionRepository.findById(id);

        if (reservacionSearch.isEmpty()) {
            throw new Exception("Reservacion no encontrada.");
        }

        return reservacionSearch.get();
    }

    public Factura getFacturaByReservacionId(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Id invalido.");
        }

        Optional<Factura> facturaSearch = this.facturaRepository.findByReservacionId(id);

        if (facturaSearch.isEmpty()) {
            throw new Exception("Factura no encontrada.");
        }

        return facturaSearch.get();
    }

    public Cancelacion getCancelacionByReservacionId(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Id invalido.");
        }

        Optional<Cancelacion> cancelacionSearch = this.cancelacionRepository.findByReservacionId(id);

        if (cancelacionSearch.isEmpty()) {
            throw new Exception("Cancelacion no encontrada.");
        }

        return cancelacionSearch.get();
    }

    public byte[] getFactura(Long ventId) throws Exception {
        Reservacion reservacion = this.getReservacion(ventId);
        Factura factura = this.getFacturaByReservacionId(ventId);

        return this.facturaImprimible.init(reservacion, factura);
    }

    public byte[] getFacturaCancelacion(Long ventId) throws Exception {
        Reservacion reservacion = this.getReservacion(ventId);
        Factura factura = this.getFacturaByReservacionId(ventId);
        Cancelacion cancelacion = this.getCancelacionByReservacionId(ventId);

        return this.facturaCancelacionImprimible.init(reservacion, factura, cancelacion);
    }
}
