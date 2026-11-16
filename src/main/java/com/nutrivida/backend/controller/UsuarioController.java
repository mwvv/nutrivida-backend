package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Obtiene todos los usuarios", description = "Retorna la lista completa de usuarios registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public CollectionModel<EntityModel<UsuarioDTO.Response>> listar() {
        List<EntityModel<UsuarioDTO.Response>> usuarios = usuarioService.listarTodos().stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).listar()).withSelfRel());
    }

    @Operation(summary = "Obtiene un usuario por su identificador", description = "Retorna la información de un usuario específico según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recurso encontrado"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<UsuarioDTO.Response> buscar(@PathVariable Long id) {
        UsuarioDTO.Response usuario = usuarioService.buscarPorId(id).orElseThrow();
        return toModel(usuario).add(
                linkTo(methodOn(UsuarioController.class).listar()).withRel("todos-los-usuarios"));
    }

    @Operation(summary = "Registra un nuevo usuario", description = "Permite crear un nuevo usuario en el sistema; todo usuario nuevo queda con rol CLIENTE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "409", description = "El RUN o el correo ya existen")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO.Response crear(@Valid @RequestBody UsuarioDTO.Request dto) {
        return usuarioService.guardar(dto);
    }

    @Operation(summary = "Actualiza un usuario existente", description = "Modifica la información de un usuario previamente registrado; el rol no se modifica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recurso actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "El RUN o el correo ya existen")
    })
    @PutMapping("/{id}")
    public UsuarioDTO.Response actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO.Request dto) {
        return usuarioService.actualizar(id, dto).orElseThrow();
    }

    @Operation(summary = "Elimina un usuario", description = "Elimina un usuario del sistema utilizando su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "El usuario está relacionado con otros datos, por ejemplo ventas")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }

    private EntityModel<UsuarioDTO.Response> toModel(UsuarioDTO.Response usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).buscar(usuario.getId())).withSelfRel());
    }
}
