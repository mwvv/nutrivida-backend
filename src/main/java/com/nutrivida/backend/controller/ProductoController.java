package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.ProductoDTO;
import com.nutrivida.backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Productos", description = "Operaciones relacionadas con la gestión de productos y servicios")
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Obtiene todos los productos", description = "Retorna la lista completa de productos y servicios registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public List<ProductoDTO.Response> listar() {
        return productoService.listarTodos();
    }

    @Operation(summary = "Obtiene un producto por su identificador", description = "Retorna la información de un producto específico según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recurso encontrado"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
    })
    @GetMapping("/{id}")
    public ProductoDTO.Response buscar(@PathVariable Long id) {
        return productoService.buscarPorId(id).orElseThrow();
    }

    @Operation(summary = "Registra un nuevo producto", description = "Permite crear un nuevo producto o servicio en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "409", description = "El código del producto ya existe")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO.Response crear(@Valid @RequestBody ProductoDTO.Request dto) {
        return productoService.guardar(dto);
    }

    @Operation(summary = "Actualiza un producto existente", description = "Modifica la información de un producto previamente registrado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recurso actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "El código del producto ya existe")
    })
    @PutMapping("/{id}")
    public ProductoDTO.Response actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO.Request dto) {
        return productoService.actualizar(id, dto).orElseThrow();
    }

    @Operation(summary = "Elimina un producto", description = "Elimina un producto del sistema utilizando su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "El producto está relacionado con otros datos")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
