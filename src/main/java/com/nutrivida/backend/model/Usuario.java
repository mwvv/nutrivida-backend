package com.nutrivida.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 9)
    private String run;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    // Hash BCrypt (60 caracteres; columna ampliada a 100 en V3). Se recibe en el JSON pero nunca se devuelve.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String contrasena;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // FK a regiones.id, mapeada como campo simple (sin relación JPA)
    @Column(name = "region_id", nullable = false)
    private Integer regionId;

    // FK a comunas.id, mapeada como campo simple (sin relación JPA)
    @Column(name = "comuna_id", nullable = false)
    private Integer comunaId;

    @Column(nullable = false, length = 30)
    private String objetivo;

    @Column(name = "tipo_alimentacion", nullable = false, length = 15)
    private String tipoAlimentacion;

    @Column(length = 200)
    private String restricciones;

    // La columna tiene DEFAULT 'CLIENTE', pero Hibernate envía null si el JSON no trae rol;
    // el valor por defecto se asigna en UsuarioService.
    @Column(nullable = false, length = 10)
    private String rol;
}
