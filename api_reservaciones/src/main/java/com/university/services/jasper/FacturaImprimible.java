package com.university.services.jasper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.university.models.Factura;
import com.university.models.Reservacion;

import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;

@Component
public class FacturaImprimible {

    private Reservacion reservacion;
    private Factura factura;

    /*
    public byte[] init(Venta venta, DatosFacturacion datosFacturacion,
            List<LineaVenta> lineaVenta) throws Exception {
        this.reservacion = venta;
        this.factura = datosFacturacion;
        this.lineaVenta = lineaVenta;
        //si pasaron las comporbaciones mandamos a traer los parametros
        Map<String, Object> parametrosReporte = this.construirFactura();
        //mandamos ha abrir el reporte
        return this.exportarReporte("FacturaAyD", parametrosReporte,
                "pdf");
    }

    private Map<String, Object> construirFactura() throws Exception {
        //crear el mapa que contendra los parametros del reporte
        Map<String, Object> parametrosReporte = new HashMap<>();

        //mandamos a construir el desgloce
        ArrayList<DesgloceDto> desgloce = this.construirDesgloces(
                this.lineaVenta);

        //creamos un nuevo JRBeanArrayDataSource (necesario para los datos de la tabla del reporte) a partir del Set
        JRBeanArrayDataSource tablaDesgloce
                = new JRBeanArrayDataSource(desgloce.toArray());

        //anadimos los parametros al map (la key debe llamarse exactamente como los prameters en el reporte)
        parametrosReporte.put("tablaDesgloce", tablaDesgloce);
        parametrosReporte.put("total", "Q" + reservacion.getValorTotal());
        parametrosReporte.put("nombreComprador",
                factura.getNombre());
        parametrosReporte.put("cuota_pago_entrega",
                "Q" + reservacion.getCuotaPagContraEntrega());
        parametrosReporte.put("fecha", "Fecha: "
                + this.manejadorDeFecha.parsearFechaYHoraAFormatoRegional(
                        reservacion.getCreatedAt()));
        parametrosReporte.put("noItems",
                desgloce.size() + " Items");
        parametrosReporte.put("nit",
                factura.getNit());
        return parametrosReporte;
    }
    */
}
