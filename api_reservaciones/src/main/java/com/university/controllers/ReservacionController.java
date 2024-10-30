package com.university.controllers;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.EstadoReservacion;
import com.university.models.Factura;
import com.university.models.MetodoPago;
import com.university.models.Reservacion;
import com.university.models.request.CreateCancelacionDto;
import com.university.models.request.CreateReservacionDto;
import com.university.services.EstadoReservacionService;
import com.university.services.FacturaService;
import com.university.services.MetodoPagoService;
import com.university.services.ReservacionService;
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
    private ReservacionService reservacionService;
    @Autowired
    private EstadoReservacionService estadoReservacionService;
    @Autowired
    private MetodoPagoService metodoPagoService;
    @Autowired
    private FacturaService facturaService;

    @Operation(summary = "Obtener todos los estados de reservacion", description = "Obtiene la información de todos los estados de reservacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estados encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EstadoReservacion.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/reservacion/public/getEstadosReservacion")
    public ResponseEntity<?> getEstadosReservacion() {
        try {
            List<EstadoReservacion> data = estadoReservacionService.getEstadosReservacion();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener todos los metodos de pago", description = "Obtiene la información de todos los metodos de pago.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metodos encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EstadoReservacion.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/reservacion/public/getMetodosPago")
    public ResponseEntity<?> getMetodosPago() {
        try {
            List<MetodoPago> data = metodoPagoService.getMetodosPago();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear una nueva reservación", description = "Crea una nueva reservación con los datos proporcionados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservación creada", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Reservacion.class)) }),
        @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/reservacion/public/crearReservacion")
    public ResponseEntity<?> createReservacion(@RequestBody CreateReservacionDto reservacionDto) {
        try {
            Reservacion reservacion = reservacionService.crearReservacion(
                reservacionDto.getEmailUsuario(),
                reservacionDto.getIdEncargado(),
                reservacionDto.getIdServicio(),
                reservacionDto.getNombreUnidadRecurso(),
                reservacionDto.getNombreRecurso(),
                null, // El ID de pago se generará al crear el pago.
                reservacionDto.getHoraInicio(),
                reservacionDto.getHoraFinal(),
                reservacionDto.getFecha(),
                reservacionDto.getNombreMetodoPago(),
                reservacionDto.getNumeroPago(),
                reservacionDto.getMontoPago()
            );

            return new ApiBaseTransformer(HttpStatus.CREATED, "Reservación creada", reservacion, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error al crear la reservación", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Completar una reservación", description = "Marca una reservación como completada y genera una factura.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación completada y factura generada", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/reservacion/private/completarReservacion")
    public ResponseEntity<?> completarReservacion(@RequestParam Long idReservacion) {
        try {
            Factura factura = reservacionService.completarReservacion(idReservacion);
            return new ApiBaseTransformer(HttpStatus.OK, "Reservación completada y factura generada", factura, null, null).sendResponse();
        } catch (EntityNotFoundException ex) {
            return new ApiBaseTransformer(HttpStatus.NOT_FOUND, "Reservación no encontrada", null, null, ex.getMessage()).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Cancelar una reservación", description = "Permite cancelar una reservación, generando una factura con el monto de reembolso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación cancelada exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/reservacion/public/cancelarReservacion")
    public ResponseEntity<?> cancelarReservacion(@RequestBody CreateCancelacionDto cancelacionRequest) {
        try {
            String resultado = reservacionService.cancelarReservacion(
            cancelacionRequest.getIdReservacion(), cancelacionRequest.getMotivoCancelacion(),
            cancelacionRequest.getFechaCancelacion());
            return new ApiBaseTransformer(HttpStatus.OK, "Reservacion cancelada exitosamente y factura generada.", resultado, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtiene una factura en pdf")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Se obtiene una factura en pdf"),
        @ApiResponse(responseCode = "400", description = "Error en cualquier parte de la busqueda")
    })
    @GetMapping("/reservacion/public/factura/{id}")
    @ResponseBody
    public ResponseEntity<?> getFactura(@PathVariable Long id) {
        try {
            byte[] factura = facturaService.getFactura(id);
            // Configuramos los headers de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "Factura.pdf");
            // Devolvemos el PDF en la respuesta HTTP
            return new ResponseEntity<>(factura, headers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST,
                    null, null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtiene una factura de una cancelacion en pdf")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Se obtiene una factura en pdf"),
        @ApiResponse(responseCode = "400", description = "Error en cualquier parte de la busqueda")
    })
    @GetMapping("/reservacion/public/cancelacion/{id}")
    @ResponseBody
    public ResponseEntity<?> getCancelacion(@PathVariable Long id) {
        try {
            byte[] factura = facturaService.getFacturaCancelacion(id);
            // Configuramos los headers de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "FacturaCancelacion.pdf");
            // Devolvemos el PDF en la respuesta HTTP
            return new ResponseEntity<>(factura, headers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST,
                    null, null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtiene un comprobante de reservacion en pdf")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Se obtiene un comprobante en pdf"),
        @ApiResponse(responseCode = "400", description = "Error en cualquier parte de la busqueda")
    })
    @GetMapping("/reservacion/public/comprobante/{id}")
    @ResponseBody
    public ResponseEntity<?> getComprobante(@PathVariable Long id) {
        try {
            byte[] comprobante = reservacionService.getComprobante(id);
            // Configuramos los headers de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "FacturaCancelacion.pdf");
            // Devolvemos el PDF en la respuesta HTTP
            return new ResponseEntity<>(comprobante, headers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST,
                    null, null, null, ex.getMessage()).sendResponse();
        }
    }
}
