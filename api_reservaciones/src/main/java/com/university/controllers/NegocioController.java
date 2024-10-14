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

import com.university.models.Negocio;
import com.university.services.NegocioService;
import com.university.transformers.ApiBaseTransformer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Negocios", description = "Operaciones para administrar la configuracion del sitio")
public class NegocioController {

    @Autowired
    private NegocioService negocioService;

    @Operation(summary = "Obtener negocio por ID", description = "Obtiene la información del negocio basado en el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Negocio encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Negocio.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/negocio/public/{id}")
    public ResponseEntity<?> getNegocio(@PathVariable Long id) {
        try {
            Negocio data = negocioService.getNegocio(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener todos los negocios", description = "Obtiene la información de todos los negocios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Negocios encontrados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Negocio.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/negocio/public/getNegocios")
    public ResponseEntity<?> getNegocios() {
        try {
            List<Negocio> data = negocioService.getNegocios();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear negocio", description = "Crea un nuevo negocio en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Negocio creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Negocio.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/negocio/private/crearNegocio")
    public ResponseEntity<?> crearNegocio(@RequestBody Negocio crear) {
        try {
            Negocio respuesta = negocioService.createNegocio(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
