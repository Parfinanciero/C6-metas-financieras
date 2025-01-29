package com.riwi.goals.controller;

import com.riwi.goals.application.dtos.response.AdviceResponse;
import com.riwi.goals.application.services.impl.AdviceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/advice")
@RequiredArgsConstructor
public class AdviceController {
    private final AdviceService adviceService;

    @Operation(summary = "Generar sugerencia de ahorro con datos quemados")
    @PostMapping("/{goalId}")
    public ResponseEntity<AdviceResponse> generateAdvice(@PathVariable Long goalId) {
        return ResponseEntity.ok(adviceService.generateAndSaveAdvice(goalId));
    }

    @Operation(summary = "Obtener sugerencias de una meta")
    @GetMapping("/{goalId}")
    public ResponseEntity<List<AdviceResponse>> getByGoalId(@PathVariable Long goalId) {
        return ResponseEntity.ok(adviceService.getByGoalId(goalId));
    }
}
