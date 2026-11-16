package com.nutrivida.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ProductoDTO {

    @Data
    @Schema(name = "ProductoRequest")
    public static class Request {
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 60, message = "El código no puede superar los 60 caracteres")
        private String codigo;

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres")
        private String nombre;

        @NotBlank(message = "El tipo es obligatorio")
        @Pattern(regexp = "Producto|Servicio", message = "El tipo debe ser 'Producto' o 'Servicio'")
        private String tipo;

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 1, message = "El precio debe ser al menos 1")
        @Max(value = 9999999, message = "El precio no puede superar 9999999")
        private Integer precio;

        @NotBlank(message = "La unidad es obligatoria")
        @Size(max = 30, message = "La unidad no puede superar los 30 caracteres")
        private String unidad;

        @NotBlank(message = "El resumen es obligatorio")
        @Size(min = 10, max = 120, message = "El resumen debe tener entre 10 y 120 caracteres")
        private String resumen;

        @NotBlank(message = "La descripción es obligatoria")
        private String descripcion;
    }

    @Data
    @Schema(name = "ProductoResponse")
    public static class Response {
        private Long id;
        private String codigo;
        private String nombre;
        private String tipo;
        private Integer precio;
        private String unidad;
        private String resumen;
        private String descripcion;
    }
}
