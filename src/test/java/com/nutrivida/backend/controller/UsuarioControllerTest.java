package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.security.JwtService;
import com.nutrivida.backend.security.SecurityConfig;
import com.nutrivida.backend.security.UsuarioDetailsService;
import com.nutrivida.backend.service.UsuarioService;
import com.nutrivida.backend.util.DatosFalsos;
import com.nutrivida.backend.util.Tokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, JwtService.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void listarSinTokenDevuelve401ConJson() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void listarConTokenInvalidoDevuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarConTokenExpiradoDevuelve401() throws Exception {
        JwtService emisorExpirado = new JwtService(jwtSecret, -1_000);
        String expirado = "Bearer " + emisorExpirado.generarToken("admin@duoc.cl", "ADMIN");

        mockMvc.perform(get("/api/v1/usuarios").header(HttpHeaders.AUTHORIZATION, expirado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarComoClienteDevuelve403() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.cliente(jwtService)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(usuarioService, never()).listarTodos();
    }

    @Test
    void listarComoAdminDevuelve200SinExponerContrasenas() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(
                DatosFalsos.usuarioResponse(1L, "ADMIN"), DatosFalsos.usuarioResponse(2L, "CLIENTE")));

        mockMvc.perform(get("/api/v1/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(content().string(not(containsString("contrasena"))));
    }

    @Test
    void buscarPorIdComoAdminDevuelveSelfYLinkALaLista() throws Exception {
        UsuarioDTO.Response usuario = DatosFalsos.usuarioResponse(2L, "CLIENTE");
        when(usuarioService.buscarPorId(2L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/2")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value(usuario.getCorreo()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links['todos-los-usuarios'].href").exists());
    }

    @Test
    void crearConCorreoDeDominioNoPermitidoDevuelve400() throws Exception {
        UsuarioDTO.Request request = DatosFalsos.usuarioRequest();
        request.setCorreo("alguien@hotmail.com");

        mockMvc.perform(post("/api/v1/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.correo").exists());

        verify(usuarioService, never()).guardar(any());
    }

    @Test
    void crearComoAdminDevuelve201() throws Exception {
        when(usuarioService.guardar(any(UsuarioDTO.Request.class)))
                .thenReturn(DatosFalsos.usuarioResponse(20L, "CLIENTE"));

        mockMvc.perform(post("/api/v1/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(DatosFalsos.usuarioRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(content().string(not(containsString("contrasena"))));
    }

    @Test
    void eliminarUsuarioConVentasDevuelve409EnVezDe500() throws Exception {
        doThrow(new DataIntegrityViolationException("fk_ventas_usuario")).when(usuarioService).eliminar(2L);

        mockMvc.perform(delete("/api/v1/usuarios/2")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void eliminarComoAdminDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/3")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isNoContent());

        verify(usuarioService).eliminar(3L);
    }
}
