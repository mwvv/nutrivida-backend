package com.nutrivida.backend.service;

import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.repository.UsuarioRepository;
import com.nutrivida.backend.util.DatosFalsos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void guardarAsignaRolClienteYHasheaLaContrasena() {
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        when(passwordEncoder.encode(dto.getContrasena())).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario guardado = inv.getArgument(0);
            guardado.setId(10L);
            return guardado;
        });

        UsuarioDTO.Response resultado = usuarioService.guardar(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("CLIENTE", captor.getValue().getRol());
        assertEquals("hash-bcrypt", captor.getValue().getContrasena());
        assertNotEquals(dto.getContrasena(), captor.getValue().getContrasena(), "no se guarda en texto plano");
        assertEquals(10L, resultado.getId());
        assertEquals("CLIENTE", resultado.getRol());
        assertEquals(dto.getCorreo(), resultado.getCorreo());
    }

    @Test
    void laRespuestaNoExponeLaContrasena() {
        assertThrows(NoSuchFieldException.class,
                () -> UsuarioDTO.Response.class.getDeclaredField("contrasena"));
    }

    @Test
    void listarTodosConvierteCadaUsuarioEnResponse() {
        Usuario admin = DatosFalsos.usuario(1L, "ADMIN");
        Usuario cliente = DatosFalsos.usuario(2L, "CLIENTE");
        when(usuarioRepository.findAll()).thenReturn(List.of(admin, cliente));

        List<UsuarioDTO.Response> resultado = usuarioService.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("ADMIN", resultado.get(0).getRol());
        assertEquals(cliente.getCorreo(), resultado.get(1).getCorreo());
    }

    @Test
    void buscarPorIdInexistenteDevuelveVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(usuarioService.buscarPorId(99L).isEmpty());
    }

    @Test
    void actualizarConservaElRolActual() {
        Usuario existente = DatosFalsos.usuario(1L, "ADMIN");
        UsuarioDTO.Request dto = DatosFalsos.usuarioRequest();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode(dto.getContrasena())).thenReturn("otro-hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<UsuarioDTO.Response> resultado = usuarioService.actualizar(1L, dto);

        assertTrue(resultado.isPresent());
        assertEquals("ADMIN", resultado.get().getRol(), "editar un usuario no cambia su rol");
        assertEquals(dto.getNombre(), resultado.get().getNombre());
        assertEquals("otro-hash", existente.getContrasena());
    }

    @Test
    void actualizarInexistenteDevuelveVacioYNoGuarda() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(usuarioService.actualizar(99L, DatosFalsos.usuarioRequest()).isEmpty());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void eliminarExistenteBorraElRegistro() {
        when(usuarioRepository.existsById(2L)).thenReturn(true);

        usuarioService.eliminar(2L);

        verify(usuarioRepository).deleteById(2L);
    }

    @Test
    void eliminarInexistenteLanzaNoSuchElementException() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> usuarioService.eliminar(99L));
        verify(usuarioRepository, never()).deleteById(any());
    }
}
