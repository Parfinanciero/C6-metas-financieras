package com.riwi.goals.application.mappers;

import com.riwi.goals.application.dtos.request.GoalRequest;
import com.riwi.goals.application.dtos.request.GoalUpdateRequest; // Importar GoalUpdateRequest
import com.riwi.goals.application.dtos.response.GoalResponse;
import com.riwi.goals.domain.entities.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    // Convierte GoalRequest a Goal (para crear una meta)
    Goal toEntity(GoalRequest request);

    // Convierte Goal a GoalResponse (para responder con la meta)
    GoalResponse toResponse(Goal goal);

    // Actualiza una entidad Goal con los datos de GoalUpdateRequest
    void updateEntityFromRequest(GoalUpdateRequest request, @MappingTarget Goal entity);
}
