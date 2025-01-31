package com.riwi.goals.infraestructure.persistence;

import com.riwi.goals.domain.entities.Goal;
import com.riwi.goals.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    List<Goal> findByStatusAndUserId(Status status, Long userId);

    List<Goal> findByTitleAndUserId(String title, Long userId);

    Optional<Goal> findByIdAndUserId(Long id, Long userId); // Buscamos una meta por id y userId
}
