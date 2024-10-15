package com.university.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.university.models.Recurso;
import com.university.services.RecursoService;
import com.university.transformers.ApiBaseTransformer;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Recurso", description = "Operaciones para administrar a los roles")
public class RecursoController {

    @Autowired
    private RecursoService recursoService;

    @Operation(summary = "Obtener recurso por ID", description = "Obtiene la información de un recurso con el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Recurso.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/recurso/private/{id}")
    public ResponseEntity<?> getRecurso(@PathVariable Long id) {
        try {
            Recurso data = this.recursoService.getRecurso(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear recurso", description = "Crea un nuevo recurso en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Recurso.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/recurso/private/crearRecurso")
    public ResponseEntity<?> crearRecurso(@RequestBody Recurso crear) {
        try {
            Recurso respuesta = recursoService.createRecurso(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
