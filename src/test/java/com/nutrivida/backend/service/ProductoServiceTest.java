package com.nutrivida.backend.service;

import com.nutrivida.backend.dto.ProductoDTO;
import com.nutrivida.backend.model.Producto;
import com.nutrivida.backend.repository.ProductoRepository;
import com.nutrivida.backend.util.DatosFalsos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarTodosConvierteCadaProductoEnResponse() {
        Producto p1 = DatosFalsos.producto(1L);
        Producto p2 = DatosFalsos.producto(2L);
        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductoDTO.Response> resultado = productoService.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals(p1.getCodigo(), resultado.get(0).getCodigo());
        assertEquals(p2.getPrecio(), resultado.get(1).getPrecio());
    }

    @Test
    void buscarPorIdExistenteDevuelveElProducto() {
        Producto p = DatosFalsos.producto(7L);
        when(productoRepository.findById(7L)).thenReturn(Optional.of(p));

        Optional<ProductoDTO.Response> resultado = productoService.buscarPorId(7L);

        assertTrue(resultado.isPresent());
        assertEquals(7L, resultado.get().getId());
        assertEquals(p.getNombre(), resultado.get().getNombre());
    }

    @Test
    void buscarPorIdInexistenteDevuelveVacio() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(productoService.buscarPorId(99L).isEmpty());
    }

    @Test
    void guardarCopiaLosDatosDelRequestYDevuelveElIdGenerado() {
        ProductoDTO.Request dto = DatosFalsos.productoRequest();
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto guardado = inv.getArgument(0);
            assertNull(guardado.getId(), "al crear no se debe enviar un id");
            guardado.setId(5L);
            return guardado;
        });

        ProductoDTO.Response resultado = productoService.guardar(dto);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertEquals(dto.getCodigo(), captor.getValue().getCodigo());
        assertEquals(5L, resultado.getId());
        assertEquals(dto.getPrecio(), resultado.getPrecio());
    }

    @Test
    void actualizarExistenteModificaLosCampos() {
        Producto existente = DatosFalsos.producto(3L);
        ProductoDTO.Request dto = DatosFalsos.productoRequest();
        when(productoRepository.findById(3L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<ProductoDTO.Response> resultado = productoService.actualizar(3L, dto);

        assertTrue(resultado.isPresent());
        assertEquals(3L, resultado.get().getId());
        assertEquals(dto.getNombre(), resultado.get().getNombre());
        assertEquals(dto.getResumen(), resultado.get().getResumen());
    }

    @Test
    void actualizarInexistenteDevuelveVacioYNoGuarda() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProductoDTO.Response> resultado = productoService.actualizar(99L, DatosFalsos.productoRequest());

        assertTrue(resultado.isEmpty());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminarExistenteBorraElRegistro() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminar(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminarInexistenteLanzaNoSuchElementException() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(any());
    }
}
