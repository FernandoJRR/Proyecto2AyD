package com.university.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.Servicio;
import com.university.models.UnidadRecurso;
import com.university.models.Usuario;
import com.university.models.request.CreateServicioDto;
import com.university.services.ServicioService;

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
@Tag(name = "Servicios", description = "Operaciones para administrar los Servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @Operation(summary = "Obtener todos los servicios", description = "Obtiene la información de todos los roles del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Servicio.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/servicio/public/getServicios")
    public ResponseEntity<?> getServicios() {
        try {
            List<Servicio> data = servicioService.getServicios();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener servicio por ID", description = "Obtiene la información de un servicio con el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Servicio.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/servicio/public/{id}")
    public ResponseEntity<?> getServicio(@PathVariable Long id) {
        try {
            Servicio data = servicioService.getServicio(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener servicio por ID del Negocio", description = "Obtiene la información de varios servicios con el ID del Negocio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicios encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Servicio.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/servicio/public/negocio/{id}")
    public ResponseEntity<?> getServiciosByNegocioId(@PathVariable Long id) {
        try {
            List<Servicio> data = servicioService.getServicioByNegocioId(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener encargados de un servicio por su ID", description = "Obtiene la información de los encargados de un servicio con el ID del Servicio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encargados encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Servicio.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/servicio/public/encargados/{id}")
    public ResponseEntity<?> getEncargadosByServicioId(@PathVariable Long id) {
        try {
            List<Usuario> data = servicioService.getEncargadosByServicio(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener unidades de recurso de un servicio por su ID", description = "Obtiene la información de las unidades de recurso de un servicio con el ID del Servicio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidades encontradas", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Servicio.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/servicio/public/unidadesRecurso/{id}")
    public ResponseEntity<?> getUnidadesRecursoByServicio(@PathVariable Long id) {
        try {
            List<UnidadRecurso> data = servicioService.getUnidadesRecursoByServicio(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear servicio", description = "Crea un nuevo servicio en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Servicio.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/servicio/private/crearServicio")
    public ResponseEntity<?> crearServicio(@RequestBody CreateServicioDto crear) {
        try {
            Servicio respuesta = servicioService.createServicio(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualziar servicio", description = "Actualiza un servicio en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Servicio.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PatchMapping("/servicio/private/actualizarServicio")
    public ResponseEntity<?> actualizarServicio(@RequestBody Servicio update) {
        try {
            Servicio respuesta = servicioService.updateServicio(update.getId(), update);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
