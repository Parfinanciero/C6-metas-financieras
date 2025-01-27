package com.riwi.goals.application.services.generic;

import com.riwi.goals.domain.entities.Goal;
import java.util.List;

public interface ReadByTitle<Entity, UserId, Title> {
    public List<Goal> readByTitle(Long userId, String title); // Recibe userId y título
}
