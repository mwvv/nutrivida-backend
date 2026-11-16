package com.nutrivida.backend.util;

import com.nutrivida.backend.security.JwtService;

/** Genera valores del header Authorization para los tests de controllers. */
public final class Tokens {

    private Tokens() {
    }

    public static String admin(JwtService jwtService) {
        return "Bearer " + jwtService.generarToken("admin@duoc.cl", "ADMIN");
    }

    public static String cliente(JwtService jwtService) {
        return "Bearer " + jwtService.generarToken("cliente@gmail.com", "CLIENTE");
    }
}
