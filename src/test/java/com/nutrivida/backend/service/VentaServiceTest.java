package com.nutrivida.backend.service;

import com.nutrivida.backend.dto.VentaDTO;
import com.nutrivida.backend.model.Venta;
import com.nutrivida.backend.repository.VentaRepository;
import com.nutrivida.backend.util.DatosFalsos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void guardarSinFechaUsaLaFechaDeHoy() {
        VentaDTO.Request dto = DatosFalsos.ventaRequest();
        dto.setFecha(null);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        VentaDTO.Response resultado = ventaService.guardar(dto);

        assertEquals(LocalDate.now(), resultado.getFecha());
        assertEquals(dto.getUsuarioId(), resultado.getUsuarioId());
        assertEquals(dto.getTotal(), resultado.getTotal());
    }

    @Test
    void guardarConFechaRespetaLaFechaIndicada() {
        VentaDTO.Request dto = DatosFalsos.ventaRequest();
        dto.setFecha(LocalDate.of(2026, 11, 1));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        VentaDTO.Response resultado = ventaService.guardar(dto);

        assertEquals(LocalDate.of(2026, 11, 1), resultado.getFecha());
    }

    @Test
    void listarTodosConvierteCadaVentaEnResponse() {
        Venta v1 = DatosFalsos.venta(1L);
        Venta v2 = DatosFalsos.venta(2L);
        when(ventaRepository.findAll()).thenReturn(List.of(v1, v2));

        List<VentaDTO.Response> resultado = ventaService.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals(v1.getTotal(), resultado.get(0).getTotal());
        assertEquals(v2.getUsuarioId(), resultado.get(1).getUsuarioId());
    }

    @Test
    void buscarPorIdInexistenteDevuelveVacio() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(ventaService.buscarPorId(99L).isEmpty());
    }

    @Test
    void actualizarSinFechaConservaLaFechaOriginal() {
        Venta existente = DatosFalsos.venta(4L);
        LocalDate fechaOriginal = existente.getFecha();
        VentaDTO.Request dto = DatosFalsos.ventaRequest();
        dto.setFecha(null);
        when(ventaRepository.findById(4L)).thenReturn(Optional.of(existente));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<VentaDTO.Response> resultado = ventaService.actualizar(4L, dto);

        assertTrue(resultado.isPresent());
        assertEquals(fechaOriginal, resultado.get().getFecha());
        assertEquals(dto.getTotal(), resultado.get().getTotal());
    }

    @Test
    void actualizarInexistenteDevuelveVacioYNoGuarda() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(ventaService.actualizar(99L, DatosFalsos.ventaRequest()).isEmpty());
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void eliminarExistenteBorraElRegistro() {
        when(ventaRepository.existsById(1L)).thenReturn(true);

        ventaService.eliminar(1L);

        verify(ventaRepository).deleteById(1L);
    }

    @Test
    void eliminarInexistenteLanzaNoSuchElementException() {
        when(ventaRepository.existsById(99L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> ventaService.eliminar(99L));
        verify(ventaRepository, never()).deleteById(any());
    }
}
