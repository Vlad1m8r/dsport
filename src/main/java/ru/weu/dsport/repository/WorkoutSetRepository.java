package ru.weu.dsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.weu.dsport.domain.WorkoutSet;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
}
