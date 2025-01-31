package com.riwi.goals.application.services.impl;

import com.riwi.goals.application.dtos.response.AdviceResponse;

import com.riwi.goals.application.dtos.response.GenerationResult;
import com.riwi.goals.application.exceptions.DeepSeekException;
import com.riwi.goals.application.mappers.AdviceMapper;
import com.riwi.goals.domain.entities.Advice;
import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.infraestructure.persistence.AdviceRepository;
import com.riwi.goals.infraestructure.persistence.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdviceService {

    private final GoalRepository goalRepository;
    private final AIService aiService;
    private final AdviceRepository adviceRepository;
    private final AdviceMapper adviceMapper;


    //datos quedemados para pruebas
    private double income = 2000;
    private double expenses = 1500;

    public AdviceResponse getOrGenerateAdvice(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        Optional<Advice> existingAdvice = adviceRepository.findByGoalId(goalId);

        if (existingAdvice.isPresent() && existingAdvice.get().getGoal().getTargetValue() == goal.getTargetValue()) {
            // Si ya hay un consejo y el valor de la meta no ha cambiado, lo reutilizamos
            return adviceMapper.toResponse(existingAdvice.get());
        }

        return generateAndSaveAdvice(goal);
    }

    public AdviceResponse regenerateAdvice(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        adviceRepository.deleteByGoalId(goalId); // Eliminamos consejo anterior
        return generateAndSaveAdvice(goal);
    }


    private AdviceResponse generateAndSaveAdvice(Goal goal) {
        try {
            String prompt = buildPrompt(goal);
            GenerationResult result = aiService.generateAdvice(prompt);

            if (result.isError()) {
                return AdviceResponse.error(goal.getId(), result.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Advice advice = saveAdvice(goal, result);
            return adviceMapper.toResponse(advice);

        } catch (DeepSeekException e) {
            return AdviceResponse.error(goal.getId(), e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return AdviceResponse.error(goal.getId(), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildPrompt(Goal goal) {
        return String.format("""
    Eres un asesor financiero experto. Con base en los siguientes datos, proporciona exactamente 3 consejos concretos, accionables y breves para alcanzar la meta financiera especificada. 

    **Datos:**
    - **Título:** %s
    - **Objetivo financiero:** $%.2f
    - **Ingresos mensuales:** $%.2f
    - **Gastos mensuales:** $%.2f
    - **Fecha límite:** %s
    - **Descripción:** %s

    **Instrucciones:**
    - Los consejos deben ser específicos y aplicables.
    - No hagas explicaciones largas, ni respondas preguntas adicionales.
    - Formatea la respuesta en una lista numerada.
    - Dame la respuesta en español.

    Ejemplo de salida esperada:
    1. [Consejo 1]
    2. [Consejo 2]
    3. [Consejo 3]
    """,
                goal.getTitle(),
                goal.getTargetValue(),
                income,
                expenses,
                goal.getEndDate(),
                goal.getDescription());
    }


    private Advice saveAdvice(Goal goal, GenerationResult result) {
        Advice advice = Advice.builder()
                .content(result.getContent())
                .goal(goal)
                .tokenUsage(result.getTokenUsage())
                .build();
        return adviceRepository.save(advice);
    }

    public List<AdviceResponse> getAdvicesByGoalId(Long goalId) {
        Optional<Advice> advices = adviceRepository.findByGoalId(goalId);
        return advices.stream()
                .map(adviceMapper::toResponse)
                .collect(Collectors.toList());
    }




}


