package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.AuthDTO;
import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticación", description = "Inicio de sesión, registro y consulta del usuario autenticado")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Inicia sesión", description = "Valida correo y contraseña y devuelve un token JWT para usar en el header Authorization: Bearer <token>")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión correcto"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos")
    })
    @PostMapping("/login")
    public AuthDTO.Response login(@Valid @RequestBody AuthDTO.Request dto) {
        return authService.login(dto);
    }

    @Operation(summary = "Registra un nuevo cliente", description = "Crea una cuenta nueva; todo usuario registrado queda con rol CLIENTE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "409", description = "El RUN o el correo ya existen")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO.Response registrar(@Valid @RequestBody UsuarioDTO.Request dto) {
        return authService.registrar(dto);
    }

    @Operation(summary = "Obtiene el usuario autenticado", description = "Retorna los datos del usuario dueño del token enviado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado")
    })
    @GetMapping("/me")
    public UsuarioDTO.Response me(Authentication authentication) {
        return authService.perfil(authentication.getName());
    }
}
