package com.nutrivida.backend.service;

import com.nutrivida.backend.model.Producto;
import com.nutrivida.backend.repository.ProductoRepository;
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
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarTodos_devuelveLoQueEntregaElRepositorio() {
        List<Producto> productos = List.of(DatosFalsos.producto(), DatosFalsos.producto());
        when(productoRepository.findAll()).thenReturn(productos);

        assertThat(productoService.listarTodos()).isEqualTo(productos);
    }

    @Test
    void buscarPorId_cuandoExiste_devuelveElProducto() {
        Producto producto = DatosFalsos.producto();
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThat(productoService.buscarPorId(1L)).contains(producto);
    }

    @Test
    void buscarPorId_cuandoNoExiste_devuelveVacio() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(productoService.buscarPorId(99L)).isEmpty();
    }

    @Test
    void guardar_poneIdNullAntesDeGuardar() {
        Producto producto = DatosFalsos.producto();
        producto.setId(50L); // id que viene "colado" en el JSON
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto guardado = productoService.guardar(producto);

        assertThat(guardado.getId()).isNull();
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizar_cuandoExiste_guardaConElIdDeLaRuta() {
        Producto datos = DatosFalsos.producto();
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Producto> resultado = productoService.actualizar(1L, datos);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNombre()).isEqualTo(datos.getNombre());
    }

    @Test
    void actualizar_cuandoNoExiste_devuelveVacioYNoGuarda() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThat(productoService.actualizar(99L, DatosFalsos.producto())).isEmpty();
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_borraYDevuelveTrue() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        assertThat(productoService.eliminar(1L)).isTrue();
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_devuelveFalseYNoBorra() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThat(productoService.eliminar(99L)).isFalse();
        verify(productoRepository, never()).deleteById(any());
    }
}
