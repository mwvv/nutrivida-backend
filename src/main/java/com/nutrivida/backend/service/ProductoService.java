package com.nutrivida.backend.service;

import com.nutrivida.backend.dto.ProductoDTO;
import com.nutrivida.backend.model.Producto;
import com.nutrivida.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoDTO.Response> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ProductoDTO.Response> buscarPorId(Long id) {
        return productoRepository.findById(id).map(this::toResponse);
    }

    public ProductoDTO.Response guardar(ProductoDTO.Request dto) {
        Producto p = new Producto();
        copiarDatos(dto, p);
        return toResponse(productoRepository.save(p));
    }

    public Optional<ProductoDTO.Response> actualizar(Long id, ProductoDTO.Request dto) {
        return productoRepository.findById(id).map(p -> {
            copiarDatos(dto, p);
            return toResponse(productoRepository.save(p));
        });
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new NoSuchElementException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    private void copiarDatos(ProductoDTO.Request dto, Producto p) {
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setTipo(dto.getTipo());
        p.setPrecio(dto.getPrecio());
        p.setUnidad(dto.getUnidad());
        p.setResumen(dto.getResumen());
        p.setDescripcion(dto.getDescripcion());
    }

    private ProductoDTO.Response toResponse(Producto p) {
        ProductoDTO.Response r = new ProductoDTO.Response();
        r.setId(p.getId());
        r.setCodigo(p.getCodigo());
        r.setNombre(p.getNombre());
        r.setTipo(p.getTipo());
        r.setPrecio(p.getPrecio());
        r.setUnidad(p.getUnidad());
        r.setResumen(p.getResumen());
        r.setDescripcion(p.getDescripcion());
        return r;
    }
}
