package com.university.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.Rol;
import com.university.models.request.CreateRolDto;
import com.university.models.request.PermisoRolRequest;
import com.university.services.RolService;
import com.university.transformers.ApiBaseTransformer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Roles", description = "Operaciones para administrar a los roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Operation(summary = "Obtener rol por ID", description = "Obtiene la información de un rol con el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/rol/public/{id}")
    public ResponseEntity<?> getUsuario(@PathVariable Long id) {
        try {
            Rol data = rolService.getRolById(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/rol/public/crearUsuario")
    public ResponseEntity<?> crearRol(@RequestBody CreateRolDto crear) {
        try {
            Rol respuesta = rolService.createRol(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualiza los permisos de un usuario ayudante.", description = "Actualiza los permisos d eun usuario ayudante en base a su id y los id "
            + "de los permisos enviados (todo aquel permiso que no se mande se eliminara de la "
            + "lista de permisos del usuario).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/rol/private/actualizarPermisos")
    public ResponseEntity<?> actualizarPermisos(@RequestBody PermisoRolRequest updates) {
        try {
            Rol confirmacion = rolService.actualizarPermisosRol(updates);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", confirmacion, null, null).sendResponse();
        } catch (NumberFormatException ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST,
                    "Id con formato invalido",
                    null, null, ex.getMessage()).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

}
