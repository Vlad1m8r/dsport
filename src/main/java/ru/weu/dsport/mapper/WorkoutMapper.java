package ru.weu.dsport.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.SetEntry;
import ru.weu.dsport.domain.WorkoutExercise;
import ru.weu.dsport.domain.WorkoutSession;
import ru.weu.dsport.dto.WorkoutSessionResponse;
import ru.weu.dsport.dto.WorkoutSummaryResponse;

@Component
public class WorkoutMapper {

    public WorkoutSessionResponse toResponse(WorkoutSession session) {
        return toResponse(session, Map.of());
    }

    public WorkoutSessionResponse toResponse(WorkoutSession session, Map<Long, String> exerciseNames) {
        List<WorkoutSessionResponse.WorkoutExerciseResponse> exercises = session.getExercises().stream()
                .map(exercise -> toExerciseResponse(exercise, exerciseNames))
                .collect(Collectors.toList());
        Long templateId = session.getTemplate() == null ? null : session.getTemplate().getId();
        return WorkoutSessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .startedAt(session.getStartedAt())
                .finishedAt(session.getFinishedAt())
                .templateId(templateId)
                .exercises(exercises)
                .build();
    }

    public WorkoutSummaryResponse toSummaryResponse(WorkoutSession session) {
        Long templateId = session.getTemplate() == null ? null : session.getTemplate().getId();
        return WorkoutSummaryResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .startedAt(session.getStartedAt())
                .finishedAt(session.getFinishedAt())
                .templateId(templateId)
                .build();
    }

    public WorkoutSessionResponse.WorkoutExerciseResponse toExerciseResponse(WorkoutExercise exercise) {
        return toExerciseResponse(exercise, Map.of());
    }

    public WorkoutSessionResponse.WorkoutExerciseResponse toExerciseResponse(
            WorkoutExercise exercise,
            Map<Long, String> exerciseNames
    ) {
        List<WorkoutSessionResponse.SetEntryResponse> sets = exercise.getSetEntries().stream()
                .map(this::toSetResponse)
                .collect(Collectors.toList());
        return WorkoutSessionResponse.WorkoutExerciseResponse.builder()
                .id(exercise.getId())
                .exerciseId(exercise.getExercise().getId())
                .exerciseName(resolveExerciseName(exercise.getExercise(), exerciseNames))
                .exerciseType(exercise.getExercise().getType())
                .orderIndex(exercise.getOrderIndex())
                .sets(sets)
                .build();
    }

    public WorkoutSessionResponse.SetEntryResponse toSetResponse(SetEntry setEntry) {
        return WorkoutSessionResponse.SetEntryResponse.builder()
                .id(setEntry.getId())
                .orderIndex(setEntry.getOrderIndex())
                .reps(setEntry.getReps())
                .weight(setEntry.getWeight())
                .durationSeconds(setEntry.getDurationSeconds())
                .build();
    }

    private String resolveExerciseName(Exercise exercise, Map<Long, String> exerciseNames) {
        return exerciseNames.getOrDefault(exercise.getId(), "Exercise #" + exercise.getId());
    }
}
