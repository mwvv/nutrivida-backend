package com.nutrivida.backend.security;

import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Carga el usuario por correo para que Spring Security lo autentique. */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())   // hash BCrypt
                .roles(usuario.getRol())             // roles() antepone "ROLE_": ROLE_ADMIN / ROLE_CLIENTE
                .build();
    }
}
