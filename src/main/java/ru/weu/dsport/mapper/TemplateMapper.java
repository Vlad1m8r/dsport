package ru.weu.dsport.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.weu.dsport.domain.TemplateExercise;
import ru.weu.dsport.domain.TemplateSet;
import ru.weu.dsport.domain.WorkoutTemplate;
import ru.weu.dsport.dto.TemplateResponse;

@Component
public class TemplateMapper {

    public TemplateResponse toResponse(WorkoutTemplate template) {
        List<TemplateResponse.TemplateExerciseResponse> exercises = template.getExercises().stream()
                .map(this::toExerciseResponse)
                .collect(Collectors.toList());
        return TemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .exercises(exercises)
                .build();
    }

    private TemplateResponse.TemplateExerciseResponse toExerciseResponse(TemplateExercise exercise) {
        List<TemplateResponse.TemplateSetResponse> sets = exercise.getSets().stream()
                .map(this::toSetResponse)
                .collect(Collectors.toList());
        return TemplateResponse.TemplateExerciseResponse.builder()
                .exerciseId(exercise.getExercise().getId())
                .orderIndex(exercise.getOrderIndex())
                .sets(sets)
                .build();
    }

    private TemplateResponse.TemplateSetResponse toSetResponse(TemplateSet set) {
        return TemplateResponse.TemplateSetResponse.builder()
                .orderIndex(set.getOrderIndex())
                .plannedReps(set.getPlannedReps())
                .plannedDurationSeconds(set.getPlannedDurationSeconds())
                .build();
    }
}
