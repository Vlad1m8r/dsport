package ru.weu.dsport.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.WorkoutSession;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise", "exercises.setEntries"})
    Optional<WorkoutSession> findByIdAndUserId(Long id, Long userId);
}
