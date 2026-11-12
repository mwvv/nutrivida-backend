package com.nutrivida.backend.service;

import com.nutrivida.backend.model.Venta;
import com.nutrivida.backend.repository.VentaRepository;
import com.nutrivida.backend.util.DatosFalsos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void listarTodos_devuelveLoQueEntregaElRepositorio() {
        List<Venta> ventas = List.of(DatosFalsos.venta(), DatosFalsos.venta());
        when(ventaRepository.findAll()).thenReturn(ventas);

        assertThat(ventaService.listarTodos()).isEqualTo(ventas);
    }

    @Test
    void buscarPorId_cuandoExiste_devuelveLaVenta() {
        Venta venta = DatosFalsos.venta();
        venta.setId(1L);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThat(ventaService.buscarPorId(1L)).contains(venta);
    }

    @Test
    void buscarPorId_cuandoNoExiste_devuelveVacio() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(ventaService.buscarPorId(99L)).isEmpty();
    }

    @Test
    void guardar_poneIdNullAntesDeGuardar() {
        Venta venta = DatosFalsos.venta();
        venta.setId(50L); // id que viene "colado" en el JSON
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        Venta guardado = ventaService.guardar(venta);

        assertThat(guardado.getId()).isNull();
        verify(ventaRepository).save(venta);
    }

    @Test
    void actualizar_cuandoExiste_guardaConElIdDeLaRuta() {
        Venta datos = DatosFalsos.venta();
        when(ventaRepository.existsById(1L)).thenReturn(true);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Venta> resultado = ventaService.actualizar(1L, datos);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getTotal()).isEqualTo(datos.getTotal());
    }

    @Test
    void actualizar_cuandoNoExiste_devuelveVacioYNoGuarda() {
        when(ventaRepository.existsById(99L)).thenReturn(false);

        assertThat(ventaService.actualizar(99L, DatosFalsos.venta())).isEmpty();
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_borraYDevuelveTrue() {
        when(ventaRepository.existsById(1L)).thenReturn(true);

        assertThat(ventaService.eliminar(1L)).isTrue();
        verify(ventaRepository).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_devuelveFalseYNoBorra() {
        when(ventaRepository.existsById(99L)).thenReturn(false);

        assertThat(ventaService.eliminar(99L)).isFalse();
        verify(ventaRepository, never()).deleteById(any());
    }
}
