package com.university.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.Recurso;
import com.university.models.UnidadRecurso;
import com.university.services.UnidadRecursoService;
import com.university.transformers.ApiBaseTransformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Unidad de Recurso", description = "Operaciones para administrar las Unidades de Recursos")
public class UnidadRecursoController {

    @Autowired
    private UnidadRecursoService unidadRecursoService;

    @Operation(summary = "Obtener unidad de recurso por ID", description = "Obtiene la información de una unidad de recurso con el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadRecurso.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/unidad-recurso/private/{id}")
    public ResponseEntity<?> getUnidadRecurso(@PathVariable Long id) {
        try {
            UnidadRecurso data = this.unidadRecursoService.getUnidadRecurso(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear unidad de recurso", description = "Crea una nueva unidad de recurso en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad creada exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadRecurso.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/unidad-recurso/private/crearUnidadRecurso")
    public ResponseEntity<?> crearUnidadRecurso(@RequestBody UnidadRecurso crear) {
        try {
            UnidadRecurso respuesta = unidadRecursoService.createUnidadRecurso(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener unidad de recurso por ID del Recurso", description = "Obtiene la información de una unidad de recurso con el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidades encontradas", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadRecurso.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/unidad-recurso/private/recurso/{id}")
    public List<UnidadRecurso> obtenerUnidadesPorRecurso(@PathVariable Long id) {
        return unidadRecursoService.getUnidadRecursoByRecursoId(id);
    }

    @Operation(summary = "Obtener todas las unidad de recurso", description = "Obtiene la información de una unidad de recurso con el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidades encontradas", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadRecurso.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/unidad-recurso/private/getUnidadesRecurso")
    public List<UnidadRecurso> obtenerTodasUnidadesPorRecurso() {
        return unidadRecursoService.getUnidadRecurso();
    }
}
