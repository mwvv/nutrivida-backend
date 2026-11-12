package com.nutrivida.backend.service;

import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.repository.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void listarTodos_devuelveLoQueEntregaElRepositorio() {
        List<Usuario> usuarios = List.of(DatosFalsos.usuario(), DatosFalsos.usuario());
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        assertThat(usuarioService.listarTodos()).isEqualTo(usuarios);
    }

    @Test
    void buscarPorId_cuandoExiste_devuelveElUsuario() {
        Usuario usuario = DatosFalsos.usuario();
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThat(usuarioService.buscarPorId(1L)).contains(usuario);
    }

    @Test
    void buscarPorId_cuandoNoExiste_devuelveVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(usuarioService.buscarPorId(99L)).isEmpty();
    }

    @Test
    void guardar_poneIdNullAntesDeGuardar() {
        Usuario usuario = DatosFalsos.usuario();
        usuario.setId(50L);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario guardado = usuarioService.guardar(usuario);

        assertThat(guardado.getId()).isNull();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void guardar_cuandoRolEsNull_asignaCliente() {
        Usuario usuario = DatosFalsos.usuario();
        usuario.setRol(null);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(usuarioService.guardar(usuario).getRol()).isEqualTo("CLIENTE");
    }

    @Test
    void guardar_cuandoTraeRol_loRespeta() {
        Usuario usuario = DatosFalsos.usuario();
        usuario.setRol("ADMIN");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(usuarioService.guardar(usuario).getRol()).isEqualTo("ADMIN");
    }

    @Test
    void actualizar_cuandoExiste_guardaConElIdDeLaRutaYConservaElRol() {
        Usuario existente = DatosFalsos.usuario();
        existente.setId(1L);
        existente.setRol("ADMIN");
        Usuario datos = DatosFalsos.usuario();
        datos.setRol(null); // el JSON no trae rol
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Usuario> resultado = usuarioService.actualizar(1L, datos);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getCorreo()).isEqualTo(datos.getCorreo());
        assertThat(resultado.get().getRol()).isEqualTo("ADMIN");
    }

    @Test
    void actualizar_cuandoNoExiste_devuelveVacioYNoGuarda() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(usuarioService.actualizar(99L, DatosFalsos.usuario())).isEmpty();
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_borraYDevuelveTrue() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        assertThat(usuarioService.eliminar(1L)).isTrue();
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_devuelveFalseYNoBorra() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThat(usuarioService.eliminar(99L)).isFalse();
        verify(usuarioRepository, never()).deleteById(any());
    }
}
