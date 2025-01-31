package com.riwi.goals.application.dtos.response;

import com.riwi.goals.domain.entities.TokenUsage;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationResult {
    private String content;
    private TokenUsage tokenUsage;
    private boolean error;
    private String errorMessage;
}

