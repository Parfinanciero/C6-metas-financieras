package com.riwi.goals.goalsTest;

import com.riwi.goals.application.dtos.exception.InvalidRequestException;
import com.riwi.goals.application.dtos.request.GoalRequest;
import com.riwi.goals.application.services.impl.GoalService;
import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.domain.enums.Status;
import com.riwi.goals.infraestructure.persistence.GoalRepository;
import com.riwi.goals.application.mappers.GoalMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Goals create")
public class GoalServiceCreateTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalService goalService;

    private GoalRequest goalRequest;
    private Goal goal;
    private Long userId;

    @BeforeEach
    void setUp() {
        goalRequest = new GoalRequest();
        goalRequest.setTitle("Learn Mockito");
        goalRequest.setDescription("Learn to write unit tests with Mockito");
        goalRequest.setEndDate(LocalDate.parse("2025-12-31"));

        goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Learn Mockito");
        goal.setDescription("Learn to write unit tests with Mockito");
        goal.setStatus(Status.CREATED);

        userId = 1L;
    }

    @Test
    @DisplayName("Should create a goal successfully")
    void testCreate_Success() {
        when(goalMapper.toEntity(goalRequest)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        Goal result = goalService.create(goalRequest, userId);


        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("Learn Mockito", result.getTitle()),
                () -> assertEquals(Status.CREATED, result.getStatus()),
                () -> assertEquals(userId, result.getUserId())
        );

        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when GoalRequest is invalid")
    void testCreate_Fail_InvalidRequest() {
        GoalRequest invalidGoalRequest = new GoalRequest();
        invalidGoalRequest.setDescription("Missing title");
        invalidGoalRequest.setEndDate(LocalDate.now());

        assertThrows(InvalidRequestException.class, () -> goalService.create(invalidGoalRequest, userId));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should invoke the mapper to convert GoalRequest to Goal")
    void testCreate_MapperInvocation() {
        when(goalMapper.toEntity(goalRequest)).thenReturn(goal);

        goalService.create(goalRequest, userId);

        verify(goalMapper, times(1)).toEntity(goalRequest);
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when GoalRequest is null")
    void testCreate_Fail_NullRequest() {
        assertThrows(InvalidRequestException.class, () -> goalService.create(null, userId));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if saving the goal fails")
    void testCreate_Fail_SaveError() {
        when(goalMapper.toEntity(goalRequest)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> goalService.create(goalRequest, userId));

        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should handle specific database errors")
    void testCreate_Fail_DatabaseError() {
        when(goalMapper.toEntity(goalRequest)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> goalService.create(goalRequest, userId));
    }

    @Test
    @DisplayName("Should be idempotent when the same request is made twice")
    void testCreate_Idempotency() {
        when(goalMapper.toEntity(goalRequest)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        // Make the same request twice
        Goal firstRequest = goalService.create(goalRequest, userId);
        Goal secondRequest = goalService.create(goalRequest, userId);

        assertEquals(firstRequest, secondRequest);
        verify(goalRepository, times(2)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should handle multiple concurrent goal creation requests successfully")
    void testCreate_ConcurrentRequests() throws InterruptedException {
        int numberOfRequests = 10;
        long startTime = System.currentTimeMillis();

        when(goalMapper.toEntity(any(GoalRequest.class))).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        // Crear un número de hilos para hacer solicitudes concurrentes
        Runnable task = () -> { goalService.create(goalRequest, 1L); };

        var executorService = Executors.newFixedThreadPool(numberOfRequests);
        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(task);
        }

        // Esperar a que todos los hilos terminen
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        currentTime(startTime, numberOfRequests);

        // Verificamos que el repositorio haya sido llamado el número correcto de veces
        verify(goalRepository, times(numberOfRequests)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should handle high load and create goals within a time frame")
    void testCreate_HighLoad() {
        int numberOfRequests = 10000;
        long startTime = System.currentTimeMillis(); // Marcar el tiempo de inicio

        when(goalMapper.toEntity(any(GoalRequest.class))).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        for (int i = 0; i < numberOfRequests; i++) {
            goalService.create(goalRequest, 1L);
        }

        long elapsedTime = System.currentTimeMillis() - startTime; // Tiempo transcurrido
        System.out.println("Time taken for " + numberOfRequests + " requests: " + elapsedTime + " ms");

        // Verificar que el repositorio fue llamado el número de veces esperado
        verify(goalRepository, times(numberOfRequests)).save(any(Goal.class));

        // Verificar que el tiempo de ejecución esté dentro de un límite aceptable (ajustar según el caso)
        assertTrue(elapsedTime < 5000, "The requests took too long"); // Por ejemplo, menos de 5 segundos
    }

    @Test
    @DisplayName("Should handle sustained load over time")
    void testCreate_SustainedLoad() throws InterruptedException {
        int numberOfRequests = 5;
        long interval = 1000; // Intervalo entre solicitudes en milisegundos
        long startTime = System.currentTimeMillis();

        when(goalMapper.toEntity(any(GoalRequest.class))).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        // Enviar solicitudes con un intervalo de tiempo
        for (int i = 0; i < numberOfRequests; i++) {
            goalService.create(goalRequest, 1L);
            Thread.sleep(interval); // Espera para simular carga sostenida
        }

        currentTime(startTime, numberOfRequests);

        verify(goalRepository, times(numberOfRequests)).save(any(Goal.class));
    }

    //Contador de demora para los testeos de presion
    private void currentTime(Long startTime, int numberOfRequests){
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time taken for " + numberOfRequests + " requests: " + elapsedTime + " ms");
    }
}
