package com.nutrivida.backend.controller;

import com.nutrivida.backend.assembler.UsuarioModelAssembler;
import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.service.UsuarioService;
import com.nutrivida.backend.util.DatosFalsos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import(UsuarioModelAssembler.class) // el assembler es un @Component y @WebMvcTest no lo carga solo
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void getPorId_cuandoExiste_devuelve200ConLinks() throws Exception {
        Usuario usuario = DatosFalsos.usuario();
        usuario.setId(1L);
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value(usuario.getNombre()))
                .andExpect(jsonPath("$._links.self.href").value(endsWith("/api/v1/usuarios/1")))
                .andExpect(jsonPath("$._links.todos.href").value(endsWith("/api/v1/usuarios")));
    }

    @Test
    void getPorId_cuandoNoExiste_devuelve404() throws Exception {
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void post_devuelve201ConElUsuarioCreado() throws Exception {
        Usuario entrada = DatosFalsos.usuario();
        Usuario creado = DatosFalsos.usuario();
        creado.setId(10L);
        when(usuarioService.guardar(any(Usuario.class))).thenReturn(creado);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void delete_cuandoExiste_devuelve204() throws Exception {
        when(usuarioService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_cuandoNoExiste_devuelve404() throws Exception {
        when(usuarioService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }
}
