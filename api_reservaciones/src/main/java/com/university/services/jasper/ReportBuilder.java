package com.university.services.jasper;

import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.university.models.ConfiguracionGlobal;
import com.university.services.ConfiguracionGlobalService;
import com.university.services.Service;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Component
public class ReportBuilder extends Service {

    @Autowired
    protected ConfiguracionGlobalService configuracionGlobalService;

    /**
     * Adjunta informacion a un reporte previamente compilado y devuelve el
     * reporte ya calcado con los datos
     *
     * @param reportePath Localizacion del reporte
     * @param parametros Parametros del reporte
     * @return
     * @throws java.lang.Exception
     */
    private JasperPrint calcarReporte(String reportePath, Map parametros) throws Exception {
        //mandamos a traer la configuracion de la tienda y agregamos el logo y le nombre a los parametros
        ConfiguracionGlobal configGlobal = this.configuracionGlobalService.getConfig();
        parametros.put("nombre_tienda", configGlobal.getNombre());
        parametros.put("imagen_tienda",
                new ByteArrayInputStream(configGlobal.getImagen()));
        //cargamos el reporte
        JasperReport reporte
                = (JasperReport) JRLoader.loadObject(
                        getClass().getResource("/imprimibles/" + reportePath + ".jasper"));
        //creamos un nuevo Jasper imprimible
        return JasperFillManager.fillReport(
                reporte, parametros, new JREmptyDataSource());
    }

    /**
     * Adjunta informacion a un reporte previamente compilado y devuelve el
     * reporte ya calcado con los datos
     *
     * @param reportePath Localizacion del reporte
     * @param parametros Parametros del reporte
     * @param tipoReporte
     * @return
     * @throws java.lang.Exception
     */
    protected byte[] exportarReporte(String reportePath, Map parametros,
            String tipoReporte) throws Exception {
        return switch (tipoReporte) {
            case "excel" ->
                this.exportarExcel(reportePath, parametros);
            case "word" ->
                this.exportarWord(reportePath, parametros);
            default ->
                this.exportarPdf(reportePath, parametros);
        };
    }

    private byte[] exportarPdf(String reportePath, Map parametros) throws Exception {
        JasperPrint jasperPrint = this.calcarReporte(reportePath, parametros);
        // Exportamos el reporte a un ByteArrayOutputStream en lugar de un stream directo
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        // Obtenemos los bytes del pdf
        return out.toByteArray();
    }

    private byte[] exportarExcel(String reportePath, Map<String, Object> parametros) throws Exception {
        // Generamos el JasperPrint como lo haces para PDF
        JasperPrint jasperPrint = this.calcarReporte(reportePath, parametros);

        // Creamos un ByteArrayOutputStream para escribir los datos exportados
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Configuramos el exportador a XLSX
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

        // Configuramos opciones adicionales si es necesario (opcional)
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false); // Todas las páginas en una sola hoja si es false
        configuration.setDetectCellType(true); // Detecta tipos de datos como números
        exporter.setConfiguration(configuration);

        // Exportamos el reporte a XLSX
        exporter.exportReport();

        // Devolvemos los bytes generados
        return out.toByteArray();
    }

    private byte[] exportarWord(String reportePath, Map<String, Object> parametros) throws Exception {
        // Generamos el JasperPrint como lo haces para PDF o Excel
        JasperPrint jasperPrint = this.calcarReporte(reportePath, parametros);

        // Creamos un ByteArrayOutputStream para escribir los datos exportados
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Configuramos el exportador a DOCX
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

        // Exportamos el reporte a DOCX
        exporter.exportReport();

        // Devolvemos los bytes generados
        return out.toByteArray();
    }
}
