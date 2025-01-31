package com.riwi.goals.infraestructure.persistence;

import com.riwi.goals.domain.entities.Advice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdviceRepository extends JpaRepository<Advice, Long> {
    Optional<Advice> findByGoalId(Long goalId);
    void deleteByGoalId(Long goalId);

}
