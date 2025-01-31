package com.riwi.goals.goalsTest;

import com.riwi.goals.application.dtos.exception.ResourceNotFoundException;
import com.riwi.goals.application.services.impl.GoalService;
import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.domain.enums.Status;
import com.riwi.goals.infraestructure.persistence.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suite de Pruebas: Operaciones de Lectura en GoalService")
class GoalServiceReadTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;

    @BeforeEach
    void setUp() {
        goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Aprender Testing");
        goal.setStatus(Status.IN_PROGRESS);
        goal.setUserId(100L);
    }

    @Test
    @DisplayName("Reading by status - Success in meeting goals")
    void readByStatus_ReturnsGoalList_WhenGoalsExist() {
        Long userId = 100L;
        Status testStatus = Status.IN_PROGRESS;
        when(goalRepository.findByStatusAndUserId(testStatus, userId))
                .thenReturn(List.of(goal));

        var result = goalService.readByStatus(userId, testStatus);

        assertAll("Verificación de propiedades de la lista de metas",
                () -> assertNotNull(result, "La lista no debe ser nula"),
                () -> assertFalse(result.isEmpty(), "La lista no debe estar vacía"),
                () -> assertEquals(1, result.size(), "Debe contener exactamente 1 meta"),
                () -> assertEquals("Aprender Testing", result.get(0).getTitle(),
                        "El título debe coincidir")
        );

        verify(goalRepository).findByStatusAndUserId(testStatus, userId);
    }

    @Test
    @DisplayName("Read by status - Trigger exception when there are no targets")
    void readByStatus_ThrowsException_WhenNoGoalsFound() {
        Long userId = 999L;
        Status testStatus = Status.COMPLETED;
        when(goalRepository.findByStatusAndUserId(testStatus, userId))
                .thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> goalService.readByStatus(userId, testStatus),
                "Debe lanzar ResourceNotFoundException"
        );

        assertEquals("Goals not found with status: COMPLETED for UserId: 999",
                exception.getMessage(),
                "El mensaje de error debe coincidir");
    }

    @Test
    @DisplayName("Search by title - Success in finding goals")
    void readByTitle_ReturnsGoalList_WhenGoalsExist() {
        Long userId = 100L;
        String searchTitle = "Aprender Testing";
        when(goalRepository.findByTitleAndUserId(searchTitle, userId))
                .thenReturn(List.of(goal));

        var result = goalService.readByTitle(userId, searchTitle);

        assertAll("Verificación de resultados de búsqueda",
                () -> assertNotNull(result, "La lista no debe ser nula"),
                () -> assertEquals(1, result.size(), "Debe retornar 1 resultado"),
                () -> assertEquals(searchTitle, result.get(0).getTitle(),
                        "El título debe coincidir con la búsqueda")
        );
    }

    @Test
    @DisplayName("Search by title - Throws exception when there are no matches")
    void readByTitle_ThrowsException_WhenNoMatchesFound() {
        Long userId = 100L;
        String invalidTitle = "Título Inexistente";
        when(goalRepository.findByTitleAndUserId(invalidTitle, userId))
                .thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> goalService.readByTitle(userId, invalidTitle)
        );

        assertTrue(exception.getMessage().contains(invalidTitle),
                "El mensaje debe incluir el título buscado");
    }

    @Test
    @DisplayName("Obtain goals per user - Success in finding records")
    void readByUserId_ReturnsGoalList_WhenUserHasGoals() {
        Long userId = 100L;
        when(goalRepository.findByUserId(userId)).thenReturn(List.of(goal));

        var result = goalService.readByUserId(userId);

        assertAll("Verificación de metas del usuario",
                () -> assertNotNull(result, "La lista no debe ser nula"),
                () -> assertEquals(1, result.size(), "Debe contener 1 meta"),
                () -> assertEquals(userId, result.get(0).getUserId(),
                        "Debe pertenecer al usuario correcto")
        );
    }

    @Test
    @DisplayName("Get goals per user - Throws exception when no records exist")
    void readByUserId_ThrowsException_WhenUserHasNoGoals() {
        Long userId = 404L;
        when(goalRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> goalService.readByUserId(userId)
        );

        assertEquals("No goals found for UserId: 404",
                exception.getMessage(),
                "El mensaje debe incluir el ID de usuario");
    }

    @Test
    @DisplayName("Get specific goal - Successful in finding goal by user ID and goal")
    void readByUserIdAndGoalId_ReturnsGoal_WhenExists() {
        Long userId = 100L;
        Long goalId = 1L;
        when(goalRepository.findByIdAndUserId(goalId, userId))
                .thenReturn(Optional.of(goal));

        Goal result = goalService.readByUserIdAndGoalId(userId, goalId);

        assertAll("Verificación de meta específica",
                () -> assertNotNull(result, "El objeto no debe ser nulo"),
                () -> assertEquals(goalId, result.getId(), "El ID debe coincidir"),
                () -> assertEquals(userId, result.getUserId(),
                        "Debe pertenecer al usuario correcto")
        );
    }

    @Test
    @DisplayName("Get specific target - Throws exception when no user/target combination exists")
    void readByUserIdAndGoalId_ThrowsException_WhenCombinationNotFound() {
        Long userId = 100L;
        Long invalidGoalId = 999L;
        when(goalRepository.findByIdAndUserId(invalidGoalId, userId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> goalService.readByUserIdAndGoalId(userId, invalidGoalId)
        );

        assertAll("Verificación de mensaje de error",
                () -> assertTrue(exception.getMessage().contains("999"),
                        "Debe incluir el ID de meta"),
                () -> assertTrue(exception.getMessage().contains("100"),
                        "Debe incluir el ID de usuario")
        );
    }
}