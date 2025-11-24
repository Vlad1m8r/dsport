package ru.weu.dsport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.weu.dsport.domain.Exercise;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findAllByIsSystemTrue();
    List<Exercise> findAllByCreatorId(Long creatorId);
}
