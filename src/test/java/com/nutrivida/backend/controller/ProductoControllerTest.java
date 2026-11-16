package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.ProductoDTO;
import com.nutrivida.backend.security.JwtService;
import com.nutrivida.backend.security.SecurityConfig;
import com.nutrivida.backend.security.UsuarioDetailsService;
import com.nutrivida.backend.service.ProductoService;
import com.nutrivida.backend.util.DatosFalsos;
import com.nutrivida.backend.util.Tokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import({SecurityConfig.class, JwtService.class})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void listarEsPublicoYDevuelveLinks() throws Exception {
        when(productoService.listarTodos()).thenReturn(
                List.of(DatosFalsos.productoResponse(1L), DatosFalsos.productoResponse(2L)));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void buscarPorIdEsPublicoYDevuelveSelfYLinkALaLista() throws Exception {
        ProductoDTO.Response producto = DatosFalsos.productoResponse(1L);
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(producto.getCodigo()))
                .andExpect(jsonPath("$._links.self.href").value(org.hamcrest.Matchers.endsWith("/api/v1/productos/1")))
                .andExpect(jsonPath("$._links['todos-los-productos'].href")
                        .value(org.hamcrest.Matchers.endsWith("/api/v1/productos")));
    }

    @Test
    void buscarPorIdInexistenteDevuelve404() throws Exception {
        when(productoService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    @Test
    void crearSinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(DatosFalsos.productoRequest())))
                .andExpect(status().isUnauthorized());

        verify(productoService, never()).guardar(any());
    }

    @Test
    void crearComoClienteDevuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.cliente(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(DatosFalsos.productoRequest())))
                .andExpect(status().isForbidden());

        verify(productoService, never()).guardar(any());
    }

    @Test
    void crearComoAdminDevuelve201() throws Exception {
        ProductoDTO.Request request = DatosFalsos.productoRequest();
        ProductoDTO.Response creado = DatosFalsos.productoResponse(10L);
        when(productoService.guardar(any(ProductoDTO.Request.class))).thenReturn(creado);

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void crearConDatosInvalidosDevuelve400ConDetallePorCampo() throws Exception {
        ProductoDTO.Request request = DatosFalsos.productoRequest();
        request.setTipo("Otro");
        request.setPrecio(0);

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.detalles.tipo").exists())
                .andExpect(jsonPath("$.detalles.precio").exists());

        verify(productoService, never()).guardar(any());
    }

    @Test
    void crearConJsonMalFormadoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto no es json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearConCodigoDuplicadoDevuelve409() throws Exception {
        when(productoService.guardar(any(ProductoDTO.Request.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(DatosFalsos.productoRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void actualizarInexistenteDevuelve404() throws Exception {
        when(productoService.actualizar(eq(99L), any(ProductoDTO.Request.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/productos/99")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(DatosFalsos.productoRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarComoAdminDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }

    @Test
    void eliminarComoClienteDevuelve403() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.cliente(jwtService)))
                .andExpect(status().isForbidden());

        verify(productoService, never()).eliminar(any());
    }

    @Test
    void eliminarInexistenteDevuelve404() throws Exception {
        doThrow(new NoSuchElementException("Producto no encontrado")).when(productoService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/productos/99")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarProductoRelacionadoConVentasDevuelve409() throws Exception {
        doThrow(new DataIntegrityViolationException("FK constraint")).when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/productos/1")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.admin(jwtService)))
                .andExpect(status().isConflict());
    }
}
