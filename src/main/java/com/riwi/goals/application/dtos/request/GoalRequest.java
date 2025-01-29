package com.riwi.goals.application.dtos.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalRequest {
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Target value cannot be null")
    private Double targetValue;  // Monto objetivo

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "The start date cannot be earlier than the current date.")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "The completion date cannot be earlier than the current date.")
    private LocalDate endDate;

    private Double currentMount;  // Monto actual alcanzado (opcional en la creación)


}
