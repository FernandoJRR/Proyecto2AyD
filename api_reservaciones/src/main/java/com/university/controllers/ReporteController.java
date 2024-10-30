package com.university.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.services.ReporteService;
import com.university.transformers.ApiBaseTransformer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Reportes", description = "Operaciones para obtener reportes de servicios")
public class ReporteController {


    @Autowired
    private ReporteService reporteService;

    @Operation(summary = "Obtener top 5 servicios más solicitados entre fechas", description = "Obtiene el top 5 de servicios más solicitados entre las fechas proporcionadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/reporte/public/topServicios")
    public ResponseEntity<?> obtenerTopServicios(@RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                 @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<Map.Entry<String, Long>> data = reporteService.topServiciosSolicitados(fechaInicio, fechaFin);
            return new ApiBaseTransformer(HttpStatus.OK, "Reporte generado exitosamente", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
