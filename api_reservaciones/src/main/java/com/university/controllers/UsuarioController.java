package com.university.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.models.Usuario;
import com.university.models.dto.LoginDto;
import com.university.models.request.PasswordChange;
import com.university.models.request.RolesUsuarioRequest;
import com.university.models.request.SendRecoveryMailRequest;
import com.university.models.request.TwoFactorActivate;
import com.university.models.request.VerifyUserRequest;
import com.university.models.request.CreateUsuarioDto;
import com.university.models.request.HorariosUsuarioRequest;
import com.university.services.UsuarioService;
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
import java.util.Map;

@RestController
@RequestMapping(value = "api", produces =  MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = "Usuarios", description = "Operaciones para administrar a los usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene la información del usuario basado en el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/public/{id}")
    public ResponseEntity<?> getUsuario(@PathVariable Long id) {
        try {
            Usuario data = usuarioService.getUsuario(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene la información del usuario basado en el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Usuario.class)))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/private/getUsuarios")
    public ResponseEntity<?> getUsuarios() {
        try {
            List<Usuario> data = usuarioService.getUsuarios();
            return new ApiBaseTransformer(HttpStatus.OK, "OK", data, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Enviar correo de recuperación de contraseña", description = "Envía un correo de recuperación de contraseña al usuario basado en la dirección de correo electrónico proporcionada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/usuario/public/recuperarPasswordMail")
    public ResponseEntity<?> enviarMailDeRecuperacion(
            @Parameter(description = "Email para enviar el correo", required = true, example = "{correoElectronico:\"xd\"}")
            @RequestBody SendRecoveryMailRequest requestBody) {
        try {
            String correoElectronico = (String) requestBody.getCorreoElectronico();
            String mensaje = usuarioService.enviarMailDeRecuperacion(correoElectronico);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", mensaje, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Recuperar contraseña", description = "Recupera la contraseña del usuario utilizando el código de recuperación y nueva contraseña.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña recuperada exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PatchMapping("/usuario/public/recuperarPassword")
    public ResponseEntity<?> recuperarPassword(@RequestBody PasswordChange requestBody) {
        try {
            String respuesta = usuarioService.recuperarPassword(requestBody);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Verificar Usuario", description = "Verifica al usuario utilizando el código de verificacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario verificado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PatchMapping("/usuario/public/verificarUsuario")
    public ResponseEntity<?> verificarUsuario(@RequestBody VerifyUserRequest verify) {
        try {
            String respuesta = usuarioService.verificarUsuario(verify.getCodigoVerificacion());
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Cambiar contraseña", description = "Permite al usuario cambiar su contraseña actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PatchMapping("/usuario/public/cambioPassword")
    public ResponseEntity<?> cambiarPassword(
            @Parameter(description = "ID del usuario a buscar", required = true, example = "{id:1,password:\"xd\"}") @RequestBody Usuario requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailUsuarioAutenticado = authentication.getName();
            String respuesta = usuarioService.cambiarPassword(requestBody, emailUsuarioAutenticado);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Iniciar sesión", description = "Permite a un usuario iniciar sesión en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = LoginDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Credenciales incorrectas")
    })
    @PostMapping("/usuario/public/login")
    public ResponseEntity<?> login(
            @Parameter(description = "ID del producto a buscar", required = true, example = "{email:\"nose@nose\",password:\"xd\"}") @RequestBody Usuario login) {
        try {
            LoginDto respuesta = usuarioService.iniciarSesion(login);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @PostMapping("/usuario/public/validateTwoFactorToken")
    public ResponseEntity<?> validateTwoFactorToken(
            @Parameter(description = "Valida el token de autenticación de dos factores", required = true, example = "{email:\"user@email.com\",twoFactorCode:\"67858\"}") @RequestBody Usuario login) {
        try {
            LoginDto respuesta = usuarioService.login2FT(login);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/usuario/public/crearUsuario")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario crear) {
        try {
            LoginDto respuesta = usuarioService.crearUsuarioNormal(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear administrador", description = "Crea un nuevo administrador en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/usuario/private/crearAdministrador")
    public ResponseEntity<?> crearAdministrador(@RequestBody Usuario crear) {
        try {
            Usuario respuesta = usuarioService.crearAdministrador(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Crear ayudante", description = "Crea un nuevo ayudante en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador creado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @PostMapping("/usuario/public/crearAyudante")
    public ResponseEntity<?> crearAyudante(@RequestBody CreateUsuarioDto crear) {
        try {
            Usuario respuesta = usuarioService.crearAyudante(crear);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", respuesta, null, null).sendResponse();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Obtener perfil de usuario", description = "Obtiene el perfil del usuario basado en el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "400", description = "ID no válido")
    })
    @GetMapping("/usuario/private/all/perfil/{id}")
    public ResponseEntity<?> getPerfil(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.getUsuario(id);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", usuario, null, null).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario basado en el ID proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "ID con formato inválido")
    })
    @DeleteMapping("/usuario/private/eliminarUsuario/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailUsuarioAutenticado = authentication.getName();
            String confirmacion = usuarioService.eliminarUsuario(id, emailUsuarioAutenticado);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", confirmacion, null, null).sendResponse();
        } catch (NumberFormatException ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Id con formato invalido", null, null,
                    ex.getMessage()).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualizar usuario parcialmente", description = "Actualiza parcialmente la información del usuario basado en los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "400", description = "ID con formato inválido")
    })
    @PatchMapping("/usuario/private/all/updateUsuario")
    public ResponseEntity<?> actualizarUsuarioParcial(@RequestBody Usuario updates) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailUsuarioAutenticado = authentication.getName();
            Usuario confirmacion = usuarioService.updateUsuario(updates, emailUsuarioAutenticado);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", confirmacion, null, null).sendResponse();
        } catch (NumberFormatException ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST,
                    "Id con formato invalido",
                    null, null, ex.getMessage()).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualiza los horarios de un usuario.", description = "Actualiza los horarios de un usuario en base a su id y los id "
            + "de los horarios enviados (todo aquel horario que no se mande se eliminara de la "
            + "lista de horarios del rol).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/usuario/private/actualizarHorariosUsuario")
    public ResponseEntity<?> actualizarHorarios(@RequestBody HorariosUsuarioRequest updates) {
        try {
            Usuario confirmacion = usuarioService.actualizarHorariosUsuario(updates);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", confirmacion, null, null).sendResponse();
        } catch (NumberFormatException ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST,
                    "Id con formato invalido",
                    null, null, ex.getMessage()).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualiza los roles de un usuario.", description = "Actualiza los horarios de un usuario en base a su id y los id "
            + "de los horarios enviados (todo aquel horario que no se mande se eliminara de la "
            + "lista de horarios del rol).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/usuario/private/actualizarRolesUsuario")
    public ResponseEntity<?> actualizarRoles(@RequestBody RolesUsuarioRequest updates) {
        try {
            Usuario confirmacion = usuarioService.actualizarRolesUsuario(updates);
            return new ApiBaseTransformer(HttpStatus.OK, "OK", confirmacion, null, null).sendResponse();
        } catch (NumberFormatException ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST,
                    "Id con formato invalido",
                    null, null, ex.getMessage()).sendResponse();
        } catch (Exception ex) {
            return new ApiBaseTransformer(HttpStatus.BAD_REQUEST, "Error", null, null, ex.getMessage()).sendResponse();
        }
    }

    @Operation(summary = "Actualiza el two factor de un usuario.", description = "Actualiza el two factor del usuario segun se envie en la request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualziacion completa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiBaseTransformer.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/usuario/public/cambiarTwoFactor")
    public ResponseEntity<?> cambiarTwoFactor(@RequestBody TwoFactorActivate updates) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailUsuarioAutenticado = authentication.getName();
            String confirmacion = usuarioService.cambiarTwoFactor(updates, emailUsuarioAutenticado);
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
