package com.riwi.goals.infraestructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtValidator jwtValidator;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // Aplica la configuración a todos los endpoints
                .authorizeHttpRequests(authorize -> authorize
                        // Permitimos acceso libre a los endpoints de Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        ).permitAll() // Permitir acceso sin autenticación a Swagger
                        // Requiere autenticación para todos los demás endpoints
                        .anyRequest().authenticated() // Todos los otros endpoints requieren autenticación
                )
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF
                .httpBasic(httpBasic -> httpBasic.disable()) // Desactivamos autenticación básica
                .formLogin(formLogin -> formLogin.disable()) // Desactivamos el formulario de login
                .cors(CorsConfigurer::disable) // Deshabilitamos CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No usar sesiones
                .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class); // Agregamos el filtro de validación JWT

        return http.build();
    }
}

