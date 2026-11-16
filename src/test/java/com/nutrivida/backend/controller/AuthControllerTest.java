package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.AuthDTO;
import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.security.JwtService;
import com.nutrivida.backend.security.SecurityConfig;
import com.nutrivida.backend.security.UsuarioDetailsService;
import com.nutrivida.backend.service.AuthService;
import com.nutrivida.backend.util.DatosFalsos;
import com.nutrivida.backend.util.Tokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtService.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void loginCorrectoEsPublicoYDevuelveElToken() throws Exception {
        AuthDTO.Response respuesta = new AuthDTO.Response();
        respuesta.setToken("token-de-prueba");
        respuesta.setTipo("Bearer");
        respuesta.setCorreo("admin@duoc.cl");
        respuesta.setRol("ADMIN");
        respuesta.setExpiraEnMs(3_600_000);
        when(authService.login(any(AuthDTO.Request.class))).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@duoc.cl\",\"contrasena\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-de-prueba"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void loginConCredencialesIncorrectasDevuelve401() throws Exception {
        when(authService.login(any(AuthDTO.Request.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@duoc.cl\",\"contrasena\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Correo o contraseña incorrectos"));
    }

    @Test
    void loginSinDatosDevuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.correo").exists())
                .andExpect(jsonPath("$.detalles.contrasena").exists());

        verify(authService, never()).login(any());
    }

    @Test
    void registrarEsPublicoYCreaUnClienteSinExponerLaContrasena() throws Exception {
        UsuarioDTO.Response creado = DatosFalsos.usuarioResponse(30L, "CLIENTE");
        when(authService.registrar(any(UsuarioDTO.Request.class))).thenReturn(creado);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(DatosFalsos.usuarioRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void registrarConCorreoYaExistenteDevuelve409() throws Exception {
        when(authService.registrar(any(UsuarioDTO.Request.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(DatosFalsos.usuarioRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    void registrarConDatosInvalidosDevuelve400() throws Exception {
        UsuarioDTO.Request request = DatosFalsos.usuarioRequest();
        request.setContrasena("123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DatosFalsos.json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.contrasena").exists());

        verify(authService, never()).registrar(any());
    }

    @Test
    void meSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meConTokenDeClienteDevuelveSusDatos() throws Exception {
        UsuarioDTO.Response perfil = DatosFalsos.usuarioResponse(2L, "CLIENTE");
        when(authService.perfil("cliente@gmail.com")).thenReturn(perfil);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, Tokens.cliente(jwtService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.correo").value(perfil.getCorreo()));
    }
}
