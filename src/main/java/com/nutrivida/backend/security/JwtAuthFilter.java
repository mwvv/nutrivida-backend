package com.nutrivida.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lee "Authorization: Bearer <token>", lo valida y deja al usuario autenticado en el SecurityContext.
 * Si no hay token o es inválido, sigue sin autenticar: las reglas de SecurityConfig deciden si
 * la ruta es pública o responde 401.
 *
 * No es @Component a propósito: si lo fuera, Spring Boot también lo registraría como filtro
 * normal del servlet (fuera de la cadena de seguridad). Se crea en SecurityConfig.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(PREFIJO)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIJO.length());
        try {
            Claims claims = jwtService.validarYExtraer(token);
            String correo = claims.getSubject();

            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Se recarga desde la BD para usar el rol actual y rechazar usuarios eliminados
                UserDetails usuario = userDetailsService.loadUserByUsername(correo);
                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContext contexto = SecurityContextHolder.createEmptyContext();
                contexto.setAuthentication(autenticacion);
                SecurityContextHolder.setContext(contexto);
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // Token mal formado, firma incorrecta, expirado o usuario inexistente: no se autentica
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
