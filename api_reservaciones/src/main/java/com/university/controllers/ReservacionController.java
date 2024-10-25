package com.university.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.EstadoReservacion;
import com.university.services.EstadoReservacionService;
import com.university.transformers.ApiBaseTransformer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Reservaciones", description = "Operaciones para administrar las reservaciones del sitio")
public class ReservacionController {

    @Autowired
    private EstadoReservacionService estadoReservacionService;

    @Operation(summary = "Obtener todos los estados de reservacion", description = "Obtiene la información de todos los estados de reservacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estados encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EstadoReservacion.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/reservaciones/public/getEstadosReservacion")
    public ResponseEntity<?> getEstadosReservacion() {
        try {
            List<EstadoReservacion> data = estadoReservacionService.getEstadosReservacion();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
