package com.riwi.goals.application.dtos.request;

import com.riwi.goals.domain.enums.Status;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalUpdateRequest {
    @FutureOrPresent(message = "The completion date cannot be earlier than the current date.")
    private LocalDate endDate; // Fecha de finalización
    private Double currentMount; // Monto actual alcanzado
    private Status status; // Estado de la meta


}
