package ru.weu.dsport.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.weu.dsport.domain.WorkoutSession;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    Optional<WorkoutSession> findByIdAndUserId(Long id, Long userId);

    @Query(value = """
            select ws.id as lastWorkoutId,
                   ws.started_at as lastWorkoutStartedAt,
                   max(se.weight) as maxWeight,
                   max(se.duration_seconds) as maxDurationSeconds
            from workout_session ws
            join workout_exercise we on we.workout_session_id = ws.id
            left join set_entry se on se.workout_exercise_id = we.id
            where ws.id = (
                select ws2.id
                from workout_session ws2
                join workout_exercise we2 on we2.workout_session_id = ws2.id
                where ws2.user_id = :userId
                  and we2.exercise_id = :exerciseId
                order by ws2.started_at desc, ws2.id desc
                limit 1
            )
              and we.exercise_id = :exerciseId
            group by ws.id, ws.started_at
            """, nativeQuery = true)
    Optional<ExerciseLastMaxProjection> findLastMaxByUserIdAndExerciseId(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId
    );

    interface ExerciseLastMaxProjection {
        Long getLastWorkoutId();

        OffsetDateTime getLastWorkoutStartedAt();

        BigDecimal getMaxWeight();

        Integer getMaxDurationSeconds();
    }
}
