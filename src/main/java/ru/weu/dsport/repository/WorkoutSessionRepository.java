package ru.weu.dsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.WorkoutSession;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
}
