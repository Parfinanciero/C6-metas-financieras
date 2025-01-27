package com.riwi.goals.application.services.generic;

import com.riwi.goals.domain.entities.Goal;

public interface ReadByUserIdAndGoalId<G, L extends Number, L1 extends Number> {
    public Goal readByUserIdAndGoalId(Long userId, Long goalId);  // Recibe ambos userId y goalId
}
