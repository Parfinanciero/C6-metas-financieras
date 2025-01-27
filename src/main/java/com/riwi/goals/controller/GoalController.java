package com.riwi.goals.controller;

import com.riwi.goals.application.dtos.request.GoalRequest;
import com.riwi.goals.application.dtos.request.GoalUpdateRequest;
import com.riwi.goals.application.dtos.response.GoalResponse;
import com.riwi.goals.application.mappers.GoalMapper;
import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.domain.enums.Status;
import com.riwi.goals.domain.ports.service.IGoalService;
import com.riwi.goals.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final IGoalService goalService;
    private final GoalMapper goalMapper;


    @Operation(summary = "Crear una nueva meta", description = "Permite crear una nueva meta financiera con los datos proporcionados por el usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meta creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos proporcionados", content = @Content)
    })
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@RequestBody GoalRequest request) {
        Long userId = getUserIdFromJwt();
        Goal createdGoal = goalService.create(request, userId);
        GoalResponse response = goalMapper.toResponse(createdGoal);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todas las metas", description = "Devuelve una lista de todas las metas no eliminadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de metas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals() {
        Long userId = getUserIdFromJwt();
        List<Goal> goals = goalService.readByUserId(userId);
        List<GoalResponse> responses = goals.stream()
                .map(goalMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar metas por estado", description = "Devuelve una lista de metas basadas en el estado proporcionado (incluyendo las eliminadas).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de metas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron metas con el estado especificado", content = @Content)
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<GoalResponse>> getGoalsByStatus(@PathVariable Status status) {
        Long userId = getUserIdFromJwt();
        List<Goal> goals = goalService.readByStatus(userId, status);
        List<GoalResponse> responses = goals.stream()
                .map(goalMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar metas por título", description = "Devuelve una lista de metas basadas en el título proporcionado (incluyendo las eliminadas).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de metas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron metas con el título especificado", content = @Content)
    })
    @GetMapping("/title/{title}")
    public ResponseEntity<List<GoalResponse>> getGoalsByTitle(@PathVariable String title) {
        Long userId = getUserIdFromJwt();
        List<Goal> goals = goalService.readByTitle(userId, title);
        List<GoalResponse> responses = goals.stream()
                .map(goalMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Actualizar una meta", description = "Permite actualizar los campos autorizados de una meta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meta actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponse.class))),
            @ApiResponse(responseCode = "400", description = "No se permite actualizar metas canceladas o eliminadas", content = @Content),
            @ApiResponse(responseCode = "404", description = "Meta no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @RequestBody GoalUpdateRequest request) {
        Long userId = getUserIdFromJwt();
        Goal updatedGoal = goalService.update(id, request, userId);
        GoalResponse response = goalMapper.toResponse(updatedGoal);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar metas por ID de usuario", description = "Devuelve una lista de metas asociadas a un usuario específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de metas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron metas para el usuario con el ID especificado", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalResponse>> getGoalsByUserId(@PathVariable Long userId) {
        List<Goal> goals = goalService.readByUserId(userId);
        List<GoalResponse> responses = goals.stream()
                .map(goalMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar una meta específica por usuario y meta", description = "Devuelve una meta específica de un usuario por su ID y el ID de la meta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meta obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la meta para el usuario con el ID especificado", content = @Content)
    })
    @GetMapping("/user/{userId}/goal/{goalId}")
    public ResponseEntity<GoalResponse> getGoalByUserIdAndGoalId(@PathVariable Long userId, @PathVariable Long goalId) {
        Goal goal = goalService.readByUserIdAndGoalId(userId, goalId);
        GoalResponse response = goalMapper.toResponse(goal);
        return ResponseEntity.ok(response);
    }
}
