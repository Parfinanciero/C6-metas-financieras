package com.riwi.goals.securityTest;

import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.infraestructure.persistence.GoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@DisplayName("Security Tests")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc is used to perform requests and simulate interactions with controllers

    @Autowired
    @Mock
    private GoalRepository goalRepository;

    private static final String PUBLIC_ENDPOINT = "/swagger-ui/index.html"; // Public test endpoint
    private static final String PRIVATE_ENDPOINT = "/goals";


    private String validateToken(){
        return "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwibmFtZSI6InNlYmFzdGlhbiIsInVzZXJJZCI6MSwic3ViIjoic2ViYXN0aWFuQGdtYWlsLmNvbSIsImlhdCI6MTczODEwMjU4NywiZXhwIjoxMDM3ODEwMjU4N30.oQjJhv4OwZiqa9cVZc1TmWgYIqO2QM4feSizhPvvQL4";
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should allow access to private endpoints with valid JWT")
    void shouldAllowPrivateAccessToPrivateEndPoints() throws Exception {
        String token = validateToken();

        Goal goal = Goal.builder()
                .title("hello")
                .description("string")
                .startDate(LocalDate.of(2025, 1, 29))
                .endDate(LocalDate.of(2025, 1, 29))
                .currentMount(0.1)
                .targetValue(0.1)
                .build();

        goal.setUserId(1L);

        this.goalRepository.save(goal);

        mockMvc.perform(get(PRIVATE_ENDPOINT)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}

