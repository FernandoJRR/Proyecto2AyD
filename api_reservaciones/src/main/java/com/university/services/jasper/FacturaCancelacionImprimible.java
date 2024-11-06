package com.university.services.jasper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.university.models.Cancelacion;
import com.university.models.Factura;
import com.university.models.Reservacion;
import com.university.tools.ManejadorFecha;

import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;

@Component
public class FacturaCancelacionImprimible extends ReportBuilder{

    private Reservacion reservacion;
    private Factura factura;
    private Cancelacion cancelacion;
    private ManejadorFecha manejadorFecha;

    public byte[] init(Reservacion reservacion, Factura datosFacturacion, Cancelacion cancelacion) throws Exception {
        this.reservacion = reservacion;
        this.factura = datosFacturacion;
        this.cancelacion = cancelacion;
        this.manejadorFecha = new ManejadorFecha();
        //si pasaron las comporbaciones mandamos a traer los parametros
        Map<String, Object> parametrosReporte = this.construirFactura();
        //mandamos ha abrir el reporte
        return this.exportarReporte("FacturaCancelacion", parametrosReporte,
                "pdf");
    }

    private Map<String, Object> construirFactura() throws Exception {
        //crear el mapa que contendra los parametros del reporte
        Map<String, Object> parametrosReporte = new HashMap<>();

        parametrosReporte.put("total", "Q" + factura.getMonto());
        parametrosReporte.put("costoServicio", "Q" + reservacion.getServicio().getCosto());
        parametrosReporte.put("porcentajeReembolso", reservacion.getServicio().getPorcentaje_reembolso() + "%");
        parametrosReporte.put("nombreServicio", reservacion.getServicio().getNombre());
        parametrosReporte.put("nombreComprador",
                factura.getUsuario().getNombres());
        parametrosReporte.put("fecha", "Fecha: "
                + this.manejadorFecha.parsearFechaYHoraAFormatoRegional(
                        reservacion.getCreatedAt()));
        parametrosReporte.put("fechaCancelacion", "Fecha: "
                + this.manejadorFecha.parsearFechaYHoraAFormatoRegional(
                        cancelacion.getFechaCancelacion()));
        parametrosReporte.put("nit",
                factura.getUsuario().getNit());
        return parametrosReporte;
    }
}
