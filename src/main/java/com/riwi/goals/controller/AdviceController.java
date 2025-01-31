package com.riwi.goals.controller;

import com.riwi.goals.application.dtos.response.AdviceResponse;
import com.riwi.goals.application.services.impl.AdviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advice")
@RequiredArgsConstructor
public class AdviceController {

    private final AdviceService adviceService;

    @Operation(
            summary = "Obtener o generar consejo financiero",
            description = "Devuelve un consejo financiero basado en IA para una meta específica. Si ya existe un consejo para la meta y el objetivo no ha cambiado, lo reutiliza."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consejo obtenido o generado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Meta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/{goalId}")
    public ResponseEntity<AdviceResponse> generateAdvice(@PathVariable Long goalId) {
        AdviceResponse response = adviceService.getOrGenerateAdvice(goalId);

        if (response.isError()) {
            return ResponseEntity.status(Integer.parseInt(response.getErrorMessage())).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Regenerar consejo financiero",
            description = "Fuerza la regeneración de un consejo financiero basado en IA para una meta específica, eliminando el anterior."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consejo regenerado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Meta no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/{goalId}/regenerate")
    public ResponseEntity<AdviceResponse> regenerateAdvice(@PathVariable Long goalId) {
        AdviceResponse response = adviceService.regenerateAdvice(goalId);

        if (response.isError()) {
            return ResponseEntity.status(Integer.parseInt(response.getErrorMessage())).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar consejos de una meta",
            description = "Obtiene todos los consejos generados para una meta específica."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de consejos obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Meta no encontrada", content = @Content)
    })
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<AdviceResponse>> getAdvicesByGoal(@PathVariable Long goalId) {
        List<AdviceResponse> advices = adviceService.getAdvicesByGoalId(goalId);

        if (advices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(advices);
    }
}
