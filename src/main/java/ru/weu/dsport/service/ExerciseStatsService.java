package ru.weu.dsport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weu.dsport.dto.ExerciseLastMaxResponse;
import ru.weu.dsport.exception.NotFoundException;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.WorkoutSessionRepository;
import ru.weu.dsport.repository.WorkoutSessionRepository.ExerciseLastMaxProjection;

@Service
@RequiredArgsConstructor
public class ExerciseStatsService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final CurrentUserService currentUserService;

    public ExerciseLastMaxResponse getLastMax(Long exerciseId) {
        if (!exerciseRepository.existsById(exerciseId)) {
            throw new NotFoundException("Exercise not found");
        }

        Long userId = currentUserService.getCurrentUser().getId();
        ExerciseLastMaxProjection projection = workoutSessionRepository
                .findLastMaxByUserIdAndExerciseId(userId, exerciseId)
                .orElse(null);

        ExerciseLastMaxResponse.ExerciseLastMaxResponseBuilder responseBuilder = ExerciseLastMaxResponse.builder()
                .exerciseId(exerciseId);

        if (projection != null) {
            responseBuilder
                    .lastWorkoutId(projection.getLastWorkoutId())
                    .lastWorkoutStartedAt(projection.getLastWorkoutStartedAt())
                    .maxWeight(projection.getMaxWeight())
                    .maxDurationSeconds(projection.getMaxDurationSeconds());
        }

        return responseBuilder.build();
    }
}
