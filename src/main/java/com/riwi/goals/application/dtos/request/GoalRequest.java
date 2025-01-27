package com.riwi.goals.application.dtos.request;

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
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    private Double currentMount;  // Monto actual alcanzado (opcional en la creación)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

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
}
