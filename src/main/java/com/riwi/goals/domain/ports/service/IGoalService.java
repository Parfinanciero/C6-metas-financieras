package com.riwi.goals.domain.ports.service;

import com.riwi.goals.application.dtos.request.GoalRequest;
import com.riwi.goals.application.dtos.request.GoalUpdateRequest;
import com.riwi.goals.application.services.generic.*;
import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.domain.enums.Status;

public interface IGoalService extends
        Create<Goal, GoalRequest>, // Crear meta
        Update<Long, Goal, GoalUpdateRequest>, // Actualizar meta
        ReadByUserId<Goal, Long, Long>, // Leer metas por userId
        ReadByStatus<Goal, Long, Status>, // Leer metas por userId y estado
        ReadByTitle<Goal, Long, String>,
        ReadByUserIdAndGoalId<Goal, Long, Long> { // Leer metas por userId y título

    // Métodos adicionales si es necesario
}
