package com.nutrivida.backend.controller;

import com.nutrivida.backend.assembler.VentaModelAssembler;
import com.nutrivida.backend.model.Venta;
import com.nutrivida.backend.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Ventas", description = "Registro y gestión de ventas")
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final VentaModelAssembler ventaModelAssembler;

    public VentaController(VentaService ventaService, VentaModelAssembler ventaModelAssembler) {
        this.ventaService = ventaService;
        this.ventaModelAssembler = ventaModelAssembler;
    }

    @Operation(summary = "Listar todas las ventas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ventas")
    })
    @GetMapping
    public CollectionModel<EntityModel<Venta>> listar() {
        return ventaModelAssembler.toCollectionModel(ventaService.listarTodos());
    }

    @Operation(summary = "Obtener una venta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> buscar(@PathVariable Long id) {
        return ventaService.buscarPorId(id)
                .map(ventaModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una nueva venta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venta creada")
    })
    @PostMapping
    public ResponseEntity<Venta> crear(@RequestBody Venta venta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.guardar(venta));
    }

    @Operation(summary = "Actualizar una venta existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta actualizada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizar(@PathVariable Long id, @RequestBody Venta venta) {
        return ventaService.actualizar(id, venta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una venta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Venta eliminada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return ventaService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
