package com.riwi.goals.infraestructure.config;

import com.riwi.goals.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtValidator extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remover "Bearer " para obtener el token puro

            try {
                // Validar el token y obtener los claims
                Jws<Claims> claims = jwtUtil.validateToken(token);

                // Extraer datos del token
                String username = jwtUtil.extractUsername(claims);
                String userId = jwtUtil.extractClaim(claims, "userId").toString();

                // Crear UserDetails simulado con permisos vacíos
                UserDetails userDetails = new User(username, "", new ArrayList<>());

                // Configurar autenticación en el contexto de seguridad
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // Pasar el userId al request
                request.setAttribute("userId", userId);

            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido: " + e.getMessage());
                return;
            }
        }

        // Continuar con el filtro
        filterChain.doFilter(request, response);
    }
}
