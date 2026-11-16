package com.nutrivida.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class Request {
        @NotBlank(message = "El correo es obligatorio")
        private String correo;

        @NotBlank(message = "La contraseña es obligatoria")
        private String contrasena;
    }

    @Data
    public static class Response {
        private String token;
        private String tipo;
        private String correo;
        private String rol;
        private long expiraEnMs;
    }
}
