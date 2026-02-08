package ru.weu.dsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.WorkoutExercise;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
}
