package com.nutrivida.backend.service;

import com.nutrivida.backend.dto.AuthDTO;
import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.repository.UsuarioRepository;
import com.nutrivida.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       UsuarioService usuarioService,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    // Lanza BadCredentialsException (-> 401) si el correo o la contraseña no coinciden
    public AuthDTO.Response login(AuthDTO.Request dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getContrasena()));

        Usuario u = usuarioRepository.findByCorreo(dto.getCorreo()).orElseThrow();

        AuthDTO.Response r = new AuthDTO.Response();
        r.setToken(jwtService.generarToken(u.getCorreo(), u.getRol()));
        r.setTipo("Bearer");
        r.setCorreo(u.getCorreo());
        r.setRol(u.getRol());
        r.setExpiraEnMs(jwtService.getExpirationMs());
        return r;
    }

    // Todo usuario registrado desde aquí queda con rol CLIENTE
    public UsuarioDTO.Response registrar(UsuarioDTO.Request dto) {
        return usuarioService.guardar(dto);
    }

    public UsuarioDTO.Response perfil(String correo) {
        Usuario u = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return usuarioService.buscarPorId(u.getId()).orElseThrow();
    }
}
