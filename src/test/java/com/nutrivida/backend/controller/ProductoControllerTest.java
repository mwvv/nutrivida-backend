package com.nutrivida.backend.controller;

import com.nutrivida.backend.assembler.ProductoModelAssembler;
import com.nutrivida.backend.model.Producto;
import com.nutrivida.backend.service.ProductoService;
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

@WebMvcTest(ProductoController.class)
@Import(ProductoModelAssembler.class) // el assembler es un @Component y @WebMvcTest no lo carga solo
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void getPorId_cuandoExiste_devuelve200ConLinks() throws Exception {
        Producto producto = DatosFalsos.producto();
        producto.setId(1L);
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value(producto.getNombre()))
                .andExpect(jsonPath("$._links.self.href").value(endsWith("/api/v1/productos/1")))
                .andExpect(jsonPath("$._links.todos.href").value(endsWith("/api/v1/productos")));
    }

    @Test
    void getPorId_cuandoNoExiste_devuelve404() throws Exception {
        when(productoService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void post_devuelve201ConElProductoCreado() throws Exception {
        Producto entrada = DatosFalsos.producto();
        Producto creado = DatosFalsos.producto();
        creado.setId(10L);
        when(productoService.guardar(any(Producto.class))).thenReturn(creado);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void delete_cuandoExiste_devuelve204() throws Exception {
        when(productoService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_cuandoNoExiste_devuelve404() throws Exception {
        when(productoService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }
}
