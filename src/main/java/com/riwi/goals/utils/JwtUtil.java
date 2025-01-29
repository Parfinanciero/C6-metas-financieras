package com.riwi.goals.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String privateKey;

    //se genera la llave HMAC usando la secretKey (Base64)
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(privateKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Jws<Claims> validateToken(String token) {
        try {

            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(getSigningKey())  //se pasa la clave secreta para la verificación
                    .build()
                    .parseClaimsJws(token);  //se parsea el JWT

            System.out.println("[JwtUtils] Token validado correctamente. Payload: " + jws.getPayload());
            return jws;

        } catch (JwtException e) {
            System.err.println("[JwtUtils] Error al validar el token: " + e.getMessage());
            throw new JwtException("Token inválido: firma o payload no válido. " + e.getMessage(), e);
        }
    }

    public String extractUsername(Jws<Claims> token) {
        System.out.println("[JwtUtils] Extrayendo username: " + token.getPayload().getSubject());
        return token.getPayload().getSubject();
    }

    public Object extractClaim(Jws<Claims> token, String claim) {
        System.out.println("[JwtUtils] Extrayendo el claim '" + claim + "': " + token.getPayload().get(claim));
        return token.getPayload().get(claim);
    }

    public Map<String, Object> extractAllClaims(Jws<Claims> token) {
        System.out.println("[JwtUtils] Extrayendo todos los claims: " + token.getPayload());
        return token.getPayload();
    }
}