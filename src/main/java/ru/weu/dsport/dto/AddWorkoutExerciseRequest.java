package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление упражнения в тренировку")
public class AddWorkoutExerciseRequest {

    @NotNull
    @Schema(description = "Идентификатор упражнения", example = "123")
    private Long exerciseId;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Порядок упражнения", example = "2")
    private Integer orderIndex;
}
