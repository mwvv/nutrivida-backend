package com.nutrivida.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Datos de registro. No tiene campo "rol": si el JSON lo trae, se ignora
 * (ignoreUnknown) y AuthController siempre asigna CLIENTE.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegisterRequest(
        @NotBlank @Size(min = 7, max = 9) String run,
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Size(max = 100) String apellidos,
        @NotBlank @Email @Size(max = 100) String correo,
        @NotBlank @Size(min = 4, max = 10) String contrasena,
        LocalDate fechaNacimiento,
        @NotNull Integer regionId,
        @NotNull Integer comunaId,
        @NotBlank String objetivo,
        @NotBlank String tipoAlimentacion,
        @Size(max = 200) String restricciones) {
}
