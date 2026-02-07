package ru.weu.dsport.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.WorkoutTemplate;

public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplate, Long> {

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    List<WorkoutTemplate> findByOwnerUserIdAndDeletedAtIsNull(Long ownerUserId);

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    Optional<WorkoutTemplate> findByIdAndOwnerUserIdAndDeletedAtIsNull(Long id, Long ownerUserId);
}
