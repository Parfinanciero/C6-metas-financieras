package com.riwi.goals.application.services.impl;

import com.riwi.goals.application.dtos.exception.InvalidRequestException;
import com.riwi.goals.application.dtos.exception.ResourceNotFoundException;
import com.riwi.goals.application.dtos.request.GoalRequest;
import com.riwi.goals.application.dtos.request.GoalUpdateRequest;
import com.riwi.goals.application.mappers.GoalMapper;
import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.domain.enums.Status;
import com.riwi.goals.domain.ports.service.IGoalService;
import com.riwi.goals.infraestructure.persistence.GoalRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GoalService implements IGoalService {

    @Autowired
    private GoalRepository repository;

    @Autowired
    private GoalMapper goalMapper;

    @Override
    public Goal create(GoalRequest request, Long userId) {
        // Convertimos el DTO a la entidad y le asignamos el userId
        Goal goal = goalMapper.toEntity(request);
        goal.setStatus(Status.CREATED); // Asignamos un estado inicial
        goal.setUserId(userId); // Asignamos el userId de la solicitud
        return repository.save(goal);
    }

    @Override
    public List<Goal> readByStatus(Long userId, Status status) {
        // Buscamos metas por estado y asociadas a un userId
        List<Goal> goals = repository.findByStatusAndUserId(status, userId);
        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("Goals not found with status " + status + " for UserId: " + userId);
        }
        return goals;
    }

    @Override
    public List<Goal> readByTitle(Long userId, String title) {
        // Buscamos metas por título asociadas a un userId
        List<Goal> goals = repository.findByTitleAndUserId(title, userId);
        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("Goal not found with title: " + title + " for UserId: " + userId);
        }
        return goals;
    }

    @Override
    public Goal update(Long id, GoalUpdateRequest request, Long userId) {
        // Verificamos que la meta exista y que pertenezca al usuario
        Goal goalUpdate = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal with id " + id + " not found for UserId: " + userId));

        // Bloqueamos modificaciones a metas canceladas
        if (goalUpdate.getStatus() == Status.CANCELLED) {
            throw new InvalidRequestException("Cannot update a cancelled goal.");
        }

        // Permitimos modificar únicamente los campos autorizados
        goalUpdate.setEndDate(request.getEndDate());
        goalUpdate.setCurrentMount(request.getCurrentMount());
        goalUpdate.setStatus(request.getStatus());

        // Guardamos los cambios
        return repository.save(goalUpdate);
    }

    @Override
    public List<Goal> readByUserId(Long userId) {
        // Buscamos metas asociadas al userId
        List<Goal> goals = repository.findByUserId(userId);
        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No goals found for UserId: " + userId);
        }
        return goals;
    }

    @Override
    public Goal readByUserIdAndGoalId(Long userId, Long goalId) {
        // Usamos el repositorio para buscar la meta por userId y goalId
        return repository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with goalId " + goalId + " for UserId: " + userId));
    }

}
