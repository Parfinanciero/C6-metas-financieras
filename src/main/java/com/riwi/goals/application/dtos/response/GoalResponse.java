package com.riwi.goals.application.dtos.response;

import com.riwi.goals.domain.enums.Status;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalResponse {

    @Column( nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = true)
    private String currentMount;

    @Column(nullable = true)
    private Status status;

    @Column(nullable = false)
    private String targetValue;
}
