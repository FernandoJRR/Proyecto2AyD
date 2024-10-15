package com.university.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.TipoServicio;
import com.university.services.TipoServicioService;
import com.university.transformers.ApiBaseTransformer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Tipo de Servicio", description = "Operaciones para administrar a los tipos de servicio")
public class TipoServicioController {

    @Autowired
    private TipoServicioService tipoServicioService;

    @Operation(summary = "Obtener todos los negocios", description = "Obtiene la información de todos los negocios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Negocios encontrados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TipoServicio.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/tipo-servicio/public/getTiposServicio")
    public ResponseEntity<?> getNegocios() {
        try {
            List<TipoServicio> data = tipoServicioService.getTiposServicio();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
