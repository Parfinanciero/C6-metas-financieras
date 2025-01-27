package com.riwi.goals.application.services.generic;

public interface Update<ID, Entity, EntityRequest> {
    public Entity update(ID id, EntityRequest entityRequest, Long userId);  // Incluimos userId para verificar que la meta pertenece al usuario
}
