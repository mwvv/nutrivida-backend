package com.nutrivida.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Genera y valida tokens JWT firmados con HMAC-SHA256.
 * subject = correo del usuario, claim "rol" = ADMIN o CLIENTE.
 */
@Service
public class JwtService {

    private static final String CLAIM_ROL = "rol";

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secretoBase64,
                      @Value("${jwt.expiration-ms:3600000}") long expiracionMs) {
        // hmacShaKeyFor lanza WeakKeyException si la clave tiene menos de 256 bits
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(String correo, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(correo)
                .claim(CLAIM_ROL, rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    /** Verifica firma y expiración. Lanza JwtException si el token no es válido. */
    public Claims validarYExtraer(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esValido(String token) {
        try {
            validarYExtraer(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extraerCorreo(String token) {
        return validarYExtraer(token).getSubject();
    }

    public String extraerRol(String token) {
        return validarYExtraer(token).get(CLAIM_ROL, String.class);
    }
}
