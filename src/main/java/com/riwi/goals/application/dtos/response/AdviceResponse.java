package com.riwi.goals.application.dtos.response;

import com.riwi.goals.domain.entities.Advice;
import com.riwi.goals.domain.entities.TokenUsage;
import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdviceResponse {
    private Long adviceId;
    private Long goalId;
    private String content;
    private boolean error;
    private String errorType;
    private String errorMessage;
    private TokenUsage tokenUsage;

    public static AdviceResponse success(Advice advice) {
        return AdviceResponse.builder()
                .adviceId(advice.getId())
                .goalId(advice.getGoal().getId())
                .content(advice.getContent())
                .tokenUsage(advice.getTokenUsage())
                .build();
    }

    public static AdviceResponse error(Long goalId, String message, HttpStatus status) {
        return AdviceResponse.builder()
                .goalId(goalId)
                .error(true)
                .errorType(status.name())
                .errorMessage(message)
                .build();
    }
}

