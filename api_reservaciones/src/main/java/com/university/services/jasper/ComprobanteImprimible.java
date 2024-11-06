package com.university.services.jasper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.university.models.Reservacion;
import com.university.tools.ManejadorFecha;

@Component
public class ComprobanteImprimible extends ReportBuilder{

    private Reservacion reservacion;
    private ManejadorFecha manejadorFecha;

    public byte[] init(Reservacion reservacion) throws Exception {
        this.reservacion = reservacion;
        this.manejadorFecha = new ManejadorFecha();
        //si pasaron las comporbaciones mandamos a traer los parametros
        Map<String, Object> parametrosReporte = this.construirFactura();
        //mandamos ha abrir el reporte
        return this.exportarReporte("Comprobante", parametrosReporte,
                "pdf");
    }

    private Map<String, Object> construirFactura() throws Exception {
        //crear el mapa que contendra los parametros del reporte
        Map<String, Object> parametrosReporte = new HashMap<>();

        parametrosReporte.put("costoServicio", "Q" + reservacion.getServicio().getCosto());
        parametrosReporte.put("nombreServicio", reservacion.getServicio().getNombre());

        if (reservacion.getServicio().getTipoServicio().getNombre() == "Recurso") {
            parametrosReporte.put("nombreRecurso", reservacion.getServicio().getRecurso().getNombre());
            parametrosReporte.put("nombreUnidadRecurso", reservacion.getUnidadRecurso().getNombre());
        } else {
            parametrosReporte.put("nombreRecurso", "No aplica");
            parametrosReporte.put("nombreUnidadRecurso", "");
        }

        parametrosReporte.put("nombreComprador",
                reservacion.getUsuario().getNombres());
        parametrosReporte.put("nombreEncargado",
                reservacion.getEncargado().getNombres());
        parametrosReporte.put("fecha", "Fecha: "
                + this.manejadorFecha.parsearFechaYHoraAFormatoRegional(
                        reservacion.getCreatedAt()));
        parametrosReporte.put("nit",
                reservacion.getUsuario().getNit());
        return parametrosReporte;
    }
}
