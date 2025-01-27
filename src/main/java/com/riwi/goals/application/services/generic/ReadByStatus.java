package com.riwi.goals.application.services.generic;

import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.domain.enums.Status;
import java.util.List;

public interface ReadByStatus<Entity, UserId, Status> {
    public List<Goal> readByStatus(Long userId, Status status); // Recibe userId y status
}
