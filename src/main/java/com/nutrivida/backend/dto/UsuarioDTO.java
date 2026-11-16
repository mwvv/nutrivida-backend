package com.nutrivida.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

public class UsuarioDTO {

    @Data
    public static class Request {
        @NotBlank(message = "El RUN es obligatorio")
        @Size(min = 7, max = 9, message = "El RUN debe tener entre 7 y 9 caracteres")
        private String run;

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        private String nombre;

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        private String apellidos;

        @NotBlank(message = "El correo es obligatorio")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        @Email(message = "El correo debe tener un formato válido")
        @Pattern(regexp = "(?i)^[^@\\s]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$",
                message = "El correo debe terminar en @duoc.cl, @profesor.duoc.cl o @gmail.com")
        private String correo;

        // Contraseña en texto plano escrita por el usuario
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres")
        private String contrasena;

        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        private LocalDate fechaNacimiento;

        @NotNull(message = "La región es obligatoria")
        private Integer regionId;

        @NotNull(message = "La comuna es obligatoria")
        private Integer comunaId;

        @NotBlank(message = "El objetivo es obligatorio")
        @Pattern(regexp = "Bajar de peso|Ganar masa muscular|Mantener mi peso|Alimentarme más saludable",
                message = "El objetivo debe ser: Bajar de peso, Ganar masa muscular, Mantener mi peso o Alimentarme más saludable")
        private String objetivo;

        @NotBlank(message = "El tipo de alimentación es obligatorio")
        @Pattern(regexp = "Omnívora|Vegetariana|Vegana|Otra",
                message = "El tipo de alimentación debe ser: Omnívora, Vegetariana, Vegana u Otra")
        private String tipoAlimentacion;

        @Size(max = 200, message = "Las restricciones no pueden superar los 200 caracteres")
        private String restricciones;
    }

    @Data
    public static class Response {
        private Long id;
        private String run;
        private String nombre;
        private String apellidos;
        private String correo;
        private LocalDate fechaNacimiento;
        private Integer regionId;
        private Integer comunaId;
        private String objetivo;
        private String tipoAlimentacion;
        private String restricciones;
        private String rol;
    }
}
