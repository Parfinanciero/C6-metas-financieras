package com.riwi.goals.domain.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsage {

    private Integer promptTokens;
    private Integer completionTokens;
    private Integer cacheHitTokens;
    private Integer cacheMissTokens;
    private Integer totalTokens;

}
