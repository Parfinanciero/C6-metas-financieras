package com.riwi.goals.application.services.generic;

import java.util.List;

public interface ReadByUserId<Entity, L extends Number, L1 extends Number> {
    public List<Entity> readByUserId(Long userId);  // Recibe solo el userId y devuelve las metas asociadas
}
