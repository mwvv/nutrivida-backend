package com.nutrivida.backend.service;

import com.nutrivida.backend.dto.VentaDTO;
import com.nutrivida.backend.model.Venta;
import com.nutrivida.backend.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public List<VentaDTO.Response> listarTodos() {
        return ventaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<VentaDTO.Response> buscarPorId(Long id) {
        return ventaRepository.findById(id).map(this::toResponse);
    }

    public VentaDTO.Response guardar(VentaDTO.Request dto) {
        Venta v = new Venta();
        v.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        v.setUsuarioId(dto.getUsuarioId());
        v.setTotal(dto.getTotal());
        return toResponse(ventaRepository.save(v));
    }

    public Optional<VentaDTO.Response> actualizar(Long id, VentaDTO.Request dto) {
        return ventaRepository.findById(id).map(v -> {
            if (dto.getFecha() != null) {
                v.setFecha(dto.getFecha()); // si no viene, conserva la fecha original
            }
            v.setUsuarioId(dto.getUsuarioId());
            v.setTotal(dto.getTotal());
            return toResponse(ventaRepository.save(v));
        });
    }

    public void eliminar(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new NoSuchElementException("Venta no encontrada");
        }
        ventaRepository.deleteById(id);
    }

    private VentaDTO.Response toResponse(Venta v) {
        VentaDTO.Response r = new VentaDTO.Response();
        r.setId(v.getId());
        r.setFecha(v.getFecha());
        r.setUsuarioId(v.getUsuarioId());
        r.setTotal(v.getTotal());
        return r;
    }
}
