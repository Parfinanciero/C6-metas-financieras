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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Goals Read")
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
        goal.setTitle("Test Goal");
        goal.setStatus(Status.CREATED);
        goal.setUserId(1L);
    }

    @Test
    void readByStatus_shouldReturnGoals_whenGoalsExist() {
        // Dado que el repositorio retorna una lista de metas
        when(goalRepository.findByStatusAndUserId(Status.CREATED, 1L)).thenReturn(Collections.singletonList(goal));

        // Ejecutamos el método
        var goals = goalService.readByStatus(1L, Status.CREATED);

        // Verificamos los resultados
        assertNotNull(goals);
        assertFalse(goals.isEmpty());
        assertEquals(1, goals.size());
        assertEquals("Test Goal", goals.get(0).getTitle());

        // Verificamos que se haya llamado al repositorio correctamente
        verify(goalRepository, times(1)).findByStatusAndUserId(Status.CREATED, 1L);
    }

    @Test
    void readByStatus_shouldThrowResourceNotFoundException_whenNoGoalsExist() {
        // Dado que el repositorio no retorna metas
        when(goalRepository.findByStatusAndUserId(Status.CREATED, 1L)).thenReturn(Arrays.asList());

        // Ejecutamos y verificamos la excepción
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            goalService.readByStatus(1L, Status.CREATED);
        });

        assertEquals("Goals not found with status CREATED for UserId: 1", exception.getMessage());
    }

    @Test
    void readByTitle_shouldReturnGoals_whenGoalsExist() {
        // Dado que el repositorio retorna una lista de metas con el título "Test Goal"
        when(goalRepository.findByTitleAndUserId("Test Goal", 1L)).thenReturn(Arrays.asList(goal));

        // Ejecutamos el método
        var goals = goalService.readByTitle(1L, "Test Goal");

        // Verificamos los resultados
        assertNotNull(goals);
        assertFalse(goals.isEmpty());
        assertEquals("Test Goal", goals.get(0).getTitle());

        // Verificamos que se haya llamado al repositorio correctamente
        verify(goalRepository, times(1)).findByTitleAndUserId("Test Goal", 1L);
    }

    @Test
    void readByTitle_shouldThrowResourceNotFoundException_whenNoGoalsExist() {
        // Dado que el repositorio no retorna metas
        when(goalRepository.findByTitleAndUserId("Test Goal", 1L)).thenReturn(Arrays.asList());

        // Ejecutamos y verificamos la excepción
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            goalService.readByTitle(1L, "Test Goal");
        });

        assertEquals("Goal not found with title: Test Goal for UserId: 1", exception.getMessage());
    }

    @Test
    void readByUserId_shouldReturnGoals_whenGoalsExist() {
        // Dado que el repositorio retorna una lista de metas
        when(goalRepository.findByUserId(1L)).thenReturn(Arrays.asList(goal));

        // Ejecutamos el método
        var goals = goalService.readByUserId(1L);

        // Verificamos los resultados
        assertNotNull(goals);
        assertFalse(goals.isEmpty());
        assertEquals("Test Goal", goals.get(0).getTitle());

        // Verificamos que se haya llamado al repositorio correctamente
        verify(goalRepository, times(1)).findByUserId(1L);
    }

    @Test
    void readByUserId_shouldThrowResourceNotFoundException_whenNoGoalsExist() {
        // Dado que el repositorio no retorna metas
        when(goalRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        // Ejecutamos y verificamos la excepción
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            goalService.readByUserId(1L);
        });

        assertEquals("No goals found for UserId: 1", exception.getMessage());
    }

    @Test
    void readByUserIdAndGoalId_shouldReturnGoal_whenGoalExists() {
        // Dado que el repositorio retorna la meta correspondiente al userId y goalId
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(goal));

        // Ejecutamos el método
        var result = goalService.readByUserIdAndGoalId(1L, 1L);

        // Verificamos los resultados
        assertNotNull(result);
        assertEquals("Test Goal", result.getTitle());

        // Verificamos que se haya llamado al repositorio correctamente
        verify(goalRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    void readByUserIdAndGoalId_shouldThrowResourceNotFoundException_whenGoalDoesNotExist() {
        // Dado que el repositorio no retorna la meta correspondiente al userId y goalId
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Ejecutamos y verificamos la excepción
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            goalService.readByUserIdAndGoalId(1L, 1L);
        });

        assertEquals("Goal not found with goalId 1 for UserId: 1", exception.getMessage());
    }
}
