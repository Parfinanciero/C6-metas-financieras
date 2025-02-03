package com.riwi.goals.securityTest;

import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.infraestructure.config.SecurityConfig;
import com.riwi.goals.infraestructure.persistence.GoalRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@DisplayName("Security Tests")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoalRepository goalRepository;

    private static final String PUBLIC_ENDPOINT = "/swagger-ui/index.html";
    private static final String PRIVATE_ENDPOINT = "/goals";

    private static String token;

    @DynamicPropertySource
    static void validateToken(DynamicPropertyRegistry registry) {
        Dotenv dotenv = Dotenv.load();

        token = "Bearer " + dotenv.get("JWT_TOKEN");

        registry.add("jwt.token", () -> dotenv.get("JWT_TOKEN"));
    }

    @Test
    @DisplayName("Should allow public access to public endpoints")
    void shouldAllowPublicAccessToPublicEndPoints() throws Exception {
        mockMvc.perform(get(PUBLIC_ENDPOINT))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should deny access to private endpoints for unauthenticated users")
    void shouldDenyAccessToUnauthenticatedUsers() throws Exception {
        mockMvc.perform(get(PRIVATE_ENDPOINT))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow access to private endpoints with valid JWT")
    void shouldAllowPrivateAccessToPrivateEndPoints() throws Exception {
        // 1. Configurar el mock del repositorio
        Goal goal = Goal.builder()
                .id(1L)
                .title("hello")
                .description("string")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .currentMount(0.1)
                .targetValue(0.1)
                .userId(1L)
                .build();


        when(goalRepository.findByUserId(1L)).thenReturn(List.of(goal));

        mockMvc.perform(get(PRIVATE_ENDPOINT)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}

