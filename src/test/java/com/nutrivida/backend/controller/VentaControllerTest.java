package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.VentaDTO;
import com.nutrivida.backend.security.JwtService;
import com.nutrivida.backend.security.SecurityConfig;
import com.nutrivida.backend.security.UsuarioDetailsService;
import com.nutrivida.backend.service.VentaService;
import com.nutrivida.backend.util.DatosFalsos;
import com.nutrivida.backend.util.Tokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VentaController.class)
@Import({SecurityConfig.class, JwtService.class})
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private VentaService ventaService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void listarComoAdminDevuelve200ConLinks() throws Exception {
        when(ventaService.listarTodos()).thenReturn(
                List.of(DatosFalsos.ventaResponse(1L), DatosFalsos.ventaResponse(2L)));

        mockMvc.perform(get("/api/v1/ventas")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void listarSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarComoClienteDevuelve403() throws Exception {
        mockMvc.perform(get("/api/v1/ventas")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.cliente(jwtService)))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorIdComoAdminDevuelveSelfYLinkALaLista() throws Exception {
        VentaDTO.Response venta = DatosFalsos.ventaResponse(1L);
        when(ventaService.buscarPorId(1L)).thenReturn(Optional.of(venta));

        mockMvc.perform(get("/api/v1/ventas/1")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(venta.getTotal()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links['todas-las-ventas'].href").exists());
    }

    @Test
    void buscarPorIdInexistenteDevuelve404() throws Exception {
        when(ventaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/ventas/99")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearSinFechaEsValidoYDevuelve201() throws Exception {
        VentaDTO.Request request = DatosFalsos.ventaRequest();
        request.setFecha(null);
        when(ventaService.guardar(any(VentaDTO.Request.class))).thenReturn(DatosFalsos.ventaResponse(5L));

        mockMvc.perform(post("/api/v1/ventas")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void crearConTotalNegativoDevuelve400() throws Exception {
        VentaDTO.Request request = DatosFalsos.ventaRequest();
        request.setTotal(-100);

        mockMvc.perform(post("/api/v1/ventas")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.total").exists());

        verify(ventaService, never()).guardar(any());
    }

    @Test
    void eliminarComoAdminDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/ventas/1")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isNoContent());

        verify(ventaService).eliminar(1L);
    }
}
