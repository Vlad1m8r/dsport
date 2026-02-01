package ru.weu.dsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.WorkoutTemplate;

public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplate, Long> {
}
