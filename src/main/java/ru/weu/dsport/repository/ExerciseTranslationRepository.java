package ru.weu.dsport.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.weu.dsport.domain.ExerciseTranslation;

public interface ExerciseTranslationRepository extends JpaRepository<ExerciseTranslation, Long> {

    @Query("""
            select translation
            from ExerciseTranslation translation
            where translation.exercise.id in :exerciseIds
            order by translation.exercise.id asc, translation.id asc
            """)
    List<ExerciseTranslation> findByExerciseIds(@Param("exerciseIds") Collection<Long> exerciseIds);
}
