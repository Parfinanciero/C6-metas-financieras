package com.riwi.goals.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

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
                        ).permitAll()
                        // Permitimos acceso libre a todos los demás endpoints
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF
                .httpBasic(httpBasic -> httpBasic.disable()) // Desactivamos autenticación básica
                .formLogin(formLogin -> formLogin.disable()); // Desactivamos el formulario de login

        return http.build();
    }
}
