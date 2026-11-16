package com.nutrivida.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDTO {

    @Data
    @Schema(name = "LoginRequest")
    public static class Request {
        @NotBlank(message = "El correo es obligatorio")
        private String correo;

        @NotBlank(message = "La contraseña es obligatoria")
        private String contrasena;
    }

    @Data
    @Schema(name = "LoginResponse")
    public static class Response {
        private String token;
        private String tipo;
        private String correo;
        private String rol;
        private long expiraEnMs;
    }
}
