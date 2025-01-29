package com.riwi.goals.application.services.impl;

import com.riwi.goals.application.dtos.response.AdviceResponse;

import com.riwi.goals.domain.entities.Advice;
import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.infraestructure.persistence.AdviceRepository;
import com.riwi.goals.infraestructure.persistence.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdviceService {
    private final GoalRepository goalRepository;
    private final AIService aiService;
    private final AdviceRepository adviceRepository;

    public AdviceResponse generateAndSaveAdvice(Long goalId) {
        // Recuperar la meta desde la base de datos
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada para el ID: " + goalId));

        // Datos quemados
        double income = 2000.0;
        double expenses = 1500.0;
        double savingsPotential = income - expenses;

        // Construir el prompt dinámico utilizando los datos de la meta
        String prompt = buildPrompt(goal, income, expenses, savingsPotential);

        // Llamar a la API de OpenAI para generar el consejo
        String advice = aiService.generateAdvice(prompt);

        // Aquí podrías guardar el consejo en la base de datos si lo necesitas
        // o simplemente devolverlo como respuesta.
        return new AdviceResponse(goalId, advice);
    }

    private String buildPrompt(Goal goal, double income, double expenses, double savingsPotential) {
        return "Soy un asistente financiero. Mi usuario está trabajando en la meta '" + goal.getTitle() +
                "'con un limite de tiempo de '" + goal.getEndDate() +
                "', cuyo valor objetivo es $" + goal.getTargetValue() + ". Sus ingresos mensuales son $" + income +
                " y sus gastos mensuales son $" + expenses + ". Su capacidad de ahorro mensual es $" + savingsPotential +
                ". Proporciónale un plan de ahorro específico para alcanzar esta meta.";
    }

    public List<AdviceResponse> getByGoalId(Long goalId) {
        // Recuperar los consejos asociados a la meta desde la base de datos
        List<Advice> adviceList = adviceRepository.findByGoalId(goalId);

        // Convertir los consejos a objetos AdviceResponse para la respuesta del API
        return adviceList.stream()
                .map(advice -> new AdviceResponse(advice.getGoal().getId(), advice.getContent()))
                .collect(Collectors.toList());
    }
}

