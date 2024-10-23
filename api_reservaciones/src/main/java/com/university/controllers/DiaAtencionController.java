package com.university.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.DiaAtencion;
import com.university.services.DiaAtencionService;
import com.university.transformers.ApiBaseTransformer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Dias Atencion", description = "Operaciones para administrar a los dias")
public class DiaAtencionController {

    @Autowired
    private DiaAtencionService diaAtencionService;

    @Operation(summary = "Obtener todos los dias", description = "Obtiene la información de todos los dias del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dias encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = DiaAtencion.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/dia/public/getDias")
    public ResponseEntity<?> getDias() {
        try {
            List<DiaAtencion> data = diaAtencionService.getDiasAntecion();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
