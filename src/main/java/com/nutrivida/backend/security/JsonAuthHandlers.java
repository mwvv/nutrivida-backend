package com.nutrivida.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Respuestas 401 y 403 en JSON simple. */
public final class JsonAuthHandlers {

    private JsonAuthHandlers() {
    }

    /** 401: no hay token, o es inválido o expiró. */
    public static AuthenticationEntryPoint noAutenticado() {
        return (request, response, ex) -> escribir(response, HttpStatus.UNAUTHORIZED,
                "No autenticado: falta el token o no es válido");
    }

    /** 403: autenticado, pero sin el rol necesario. */
    public static AccessDeniedHandler accesoDenegado() {
        return (request, response, ex) -> escribir(response, HttpStatus.FORBIDDEN,
                "Acceso denegado: no tienes permisos para este recurso");
    }

    private static void escribir(HttpServletResponse response, HttpStatus estado, String mensaje)
            throws IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"status\":" + estado.value()
                        + ",\"error\":\"" + estado.getReasonPhrase()
                        + "\",\"mensaje\":\"" + mensaje + "\"}");
    }
}
