package com.riwi.goals.application.mappers;

import com.riwi.goals.application.dtos.response.AdviceResponse;
import com.riwi.goals.domain.entities.Advice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface AdviceMapper {
    @Mapping(target = "adviceId", source = "id")
    @Mapping(target = "goalId", source = "goal.id")
    AdviceResponse toResponse(Advice advice);
}
