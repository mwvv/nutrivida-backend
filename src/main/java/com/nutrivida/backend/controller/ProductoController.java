package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.ProductoDTO;
import com.nutrivida.backend.service.ProductoService;
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

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDTO.Response> listar() {
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ProductoDTO.Response buscar(@PathVariable Long id) {
        return productoService.buscarPorId(id).orElseThrow();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO.Response crear(@Valid @RequestBody ProductoDTO.Request dto) {
        return productoService.guardar(dto);
    }

    @PutMapping("/{id}")
    public ProductoDTO.Response actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO.Request dto) {
        return productoService.actualizar(id, dto).orElseThrow();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
