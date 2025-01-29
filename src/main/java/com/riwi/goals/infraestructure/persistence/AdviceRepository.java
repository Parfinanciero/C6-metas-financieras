package com.riwi.goals.infraestructure.persistence;

import com.riwi.goals.domain.entities.Advice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdviceRepository extends JpaRepository<Advice, Long> {
    List<Advice> findByGoalId(Long goalId);
}
