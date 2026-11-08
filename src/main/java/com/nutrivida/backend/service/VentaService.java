package com.nutrivida.backend.service;

import com.nutrivida.backend.model.Venta;
import com.nutrivida.backend.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public List<Venta> listarTodos() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta guardar(Venta venta) {
        venta.setId(null); // fuerza un INSERT aunque el JSON traiga un id
        return ventaRepository.save(venta);
    }

    public Optional<Venta> actualizar(Long id, Venta datos) {
        if (!ventaRepository.existsById(id)) {
            return Optional.empty();
        }
        datos.setId(id);
        return Optional.of(ventaRepository.save(datos));
    }

    public boolean eliminar(Long id) {
        if (!ventaRepository.existsById(id)) {
            return false;
        }
        ventaRepository.deleteById(id);
        return true;
    }
}
