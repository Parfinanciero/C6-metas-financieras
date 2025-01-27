package com.riwi.goals.application.services.generic;

public interface Create<Entity, EntityRequest> {
    public Entity create(EntityRequest request, Long userId);  // Agregamos userId como parámetro de tipo Long
}
