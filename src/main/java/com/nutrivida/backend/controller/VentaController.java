package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.VentaDTO;
import com.nutrivida.backend.service.VentaService;
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

@Tag(name = "Ventas", description = "Operaciones relacionadas con la gestión de ventas")
@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Operation(summary = "Obtiene todas las ventas", description = "Retorna la lista completa de ventas registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public List<VentaDTO.Response> listar() {
        return ventaService.listarTodos();
    }

    @Operation(summary = "Obtiene una venta por su identificador", description = "Retorna la información de una venta específica según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recurso encontrado"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
    })
    @GetMapping("/{id}")
    public VentaDTO.Response buscar(@PathVariable Long id) {
        return ventaService.buscarPorId(id).orElseThrow();
    }

    @Operation(summary = "Registra una nueva venta", description = "Permite crear una nueva venta; si no se indica la fecha se usa la de hoy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "409", description = "El usuario indicado no existe")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaDTO.Response crear(@Valid @RequestBody VentaDTO.Request dto) {
        return ventaService.guardar(dto);
    }

    @Operation(summary = "Actualiza una venta existente", description = "Modifica la información de una venta previamente registrada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recurso actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "El usuario indicado no existe")
    })
    @PutMapping("/{id}")
    public VentaDTO.Response actualizar(@PathVariable Long id, @Valid @RequestBody VentaDTO.Request dto) {
        return ventaService.actualizar(id, dto).orElseThrow();
    }

    @Operation(summary = "Elimina una venta", description = "Elimina una venta del sistema utilizando su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "La venta está relacionada con otros datos")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
    }
}
