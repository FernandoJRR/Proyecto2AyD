package com.university.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.university.models.ConfiguracionGlobal;
import com.university.models.dto.ConfiguracionGlobalDto;
import com.university.services.ConfiguracionGlobalService;
import com.university.transformers.ApiBaseTransformer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Configuracion Global", description = "Operaciones para administrar la configuracion del sitio")
public class ConfiguracionGlobalController {

    @Autowired
    private ConfiguracionGlobalService configuracionGlobalService;

    @Operation(summary = "Obtiene la imagen")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen obtenida exitosamente",
                content = {
                    @Content(mediaType = "image/png"),
                    @Content(mediaType = "image/jpeg"),
                    @Content(mediaType = "image/webp"),
                    @Content(mediaType = "image/gif")
                }
        ),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud",
                content = @Content)
    })
    @GetMapping("/global_config/public/getImagen")
    @ResponseBody
    public ResponseEntity<?> getImagen() {
        try {
            ConfiguracionGlobal respuesta = this.configuracionGlobalService.getConfig();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", respuesta.getMimeTypeImg()); // O ajusta según el tipo de imagen
            headers.add("Content-Disposition", "inline; filename=image."
                    + respuesta.getExtension());

            return new ResponseEntity<>(respuesta.getImagen(), headers, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Error interno del servidor.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtiene la configuracion")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuracion obtenida exitosamente",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfiguracionGlobalDto.class))
                }
        ),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud",
                content = @Content)
    })
    @GetMapping("/global_config/public/getConfig")
    @ResponseBody
    public ResponseEntity<?> getConfig() {
        try {
            ConfiguracionGlobalDto respuesta = this.configuracionGlobalService.getConfigDto();

            return new ApiBaseTransformer(HttpStatus.OK, "OK",
                    respuesta, null, null).sendResponse();

        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualiza la informacion",
            description = "Actualiza SOlO la informacion textual de la configuracion,"
            + " esto quiere decir que no se actualizara la imagen en este metodo,"
            + "para actualizar la imagen utilizar la ruta:"
            + " \"/global_config/private/actualizarImagen\"")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuracion obtenida exitosamente",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation =ConfiguracionGlobalDto.class))
                }
        ),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud",
                content = @Content)
    })
    @PatchMapping("/global_config/private/actualizarConfiguracion")
    @ResponseBody
    public ResponseEntity<?> actualizarConfiguracion(
            @Parameter(description = "Detalles de la configuracion a actualizar", required = true)
            @RequestBody ConfiguracionGlobal actualizar) {
        try {
            ConfiguracionGlobalDto respuesta = this.configuracionGlobalService.actualizarConfig(
                    actualizar);

            return new ApiBaseTransformer(HttpStatus.OK, "OK",
                    respuesta, null, null).sendResponse();

        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualiza la imagen",
            description = "Actualiza SOlO la imagen"
            + "para actualizar la demas informacion utilizar la ruta:"
            + " \"/global_config/private/actualizarConfiguracion\"")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Configuracion con la ruta de la imagen para usarse como src.",
                content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfiguracionGlobalDto.class))
                }
        ),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud",
                content = @Content)
    })
    @PostMapping("/global_config/private/actualizarImagen")
    @ResponseBody
    public ResponseEntity<?> actualizarImagen(
            @Parameter(description = "Nueva imagen", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            ConfiguracionGlobalDto respuesta = this.configuracionGlobalService.actualizarImagen(
                    file);

            return new ApiBaseTransformer(HttpStatus.OK, "OK",
                    respuesta, null, null).sendResponse();

        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }
}
