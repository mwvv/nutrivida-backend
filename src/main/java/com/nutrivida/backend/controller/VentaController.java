package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.VentaDTO;
import com.nutrivida.backend.service.VentaService;
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
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<VentaDTO.Response> listar() {
        return ventaService.listarTodos();
    }

    @GetMapping("/{id}")
    public VentaDTO.Response buscar(@PathVariable Long id) {
        return ventaService.buscarPorId(id).orElseThrow();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaDTO.Response crear(@Valid @RequestBody VentaDTO.Request dto) {
        return ventaService.guardar(dto);
    }

    @PutMapping("/{id}")
    public VentaDTO.Response actualizar(@PathVariable Long id, @Valid @RequestBody VentaDTO.Request dto) {
        return ventaService.actualizar(id, dto).orElseThrow();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
    }
}
