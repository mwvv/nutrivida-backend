package com.nutrivida.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class VentaDTO {

    @Data
    public static class Request {
        // Opcional: si no viene, se usa la fecha de hoy
        private LocalDate fecha;

        @NotNull(message = "El usuario es obligatorio")
        private Long usuarioId;

        @NotNull(message = "El total es obligatorio")
        @Min(value = 0, message = "El total no puede ser negativo")
        private Integer total;
    }

    @Data
    public static class Response {
        private Long id;
        private LocalDate fecha;
        private Long usuarioId;
        private Integer total;
    }
}
