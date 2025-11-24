package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseDto {

    @Schema(description = "Идентификатор упражнения в тренировке", example = "1")
    private Long id;

    @Schema(description = "Упражнение")
    private ExerciseDto exercise;

    @Schema(description = "Список подходов")
    private List<WorkoutSetDto> workoutSets;
}
