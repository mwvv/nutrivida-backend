package com.nutrivida.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "clave-de-prueba-para-jwt-con-mas-de-32-caracteres-123";

    private final Faker faker = new Faker();

    @Test
    void generaUnTokenQueContieneCorreoYRol() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        String correo = faker.internet().username() + "@gmail.com";

        String token = jwtService.generarToken(correo, "CLIENTE");
        Claims claims = jwtService.leerClaims(token);

        assertNotNull(token);
        assertEquals(correo, claims.getSubject());
        assertEquals("CLIENTE", claims.get("rol", String.class));
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void unTokenExpiradoEsRechazado() {
        JwtService jwtService = new JwtService(SECRET, -1_000);
        String token = jwtService.generarToken("admin@duoc.cl", "ADMIN");

        assertThrows(ExpiredJwtException.class, () -> jwtService.leerClaims(token));
    }

    @Test
    void unTokenFirmadoConOtraClaveEsRechazado() {
        JwtService emisor = new JwtService("otra-clave-distinta-con-mas-de-32-caracteres-999", 60_000);
        JwtService validador = new JwtService(SECRET, 60_000);
        String token = emisor.generarToken("admin@duoc.cl", "ADMIN");

        assertThrows(JwtException.class, () -> validador.leerClaims(token));
    }

    @Test
    void unTokenConElPayloadAlteradoEsRechazado() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        String token = jwtService.generarToken("cliente@gmail.com", "CLIENTE");
        String[] partes = token.split("\\.");
        String payloadFalso = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"cliente@gmail.com\",\"rol\":\"ADMIN\"}".getBytes(StandardCharsets.UTF_8));

        String tokenAlterado = partes[0] + "." + payloadFalso + "." + partes[2];

        assertThrows(JwtException.class, () -> jwtService.leerClaims(tokenAlterado));
    }

    @Test
    void unTextoQueNoEsUnTokenEsRechazado() {
        JwtService jwtService = new JwtService(SECRET, 60_000);

        assertThrows(JwtException.class, () -> jwtService.leerClaims("abc.def.ghi"));
    }
}
