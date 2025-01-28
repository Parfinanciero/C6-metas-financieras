package com.riwi.goals.application.dtos.request;

import com.riwi.goals.domain.enums.Status;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalUpdateRequest {
    private LocalDate endDate; // Fecha de finalización
    private Double currentMount; // Monto actual alcanzado
    private Status status; // Estado de la meta


}
