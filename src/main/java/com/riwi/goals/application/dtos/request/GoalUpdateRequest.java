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

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getCurrentMount() {
        return currentMount;
    }

    public void setCurrentMount(Double currentMount) {
        this.currentMount = currentMount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
