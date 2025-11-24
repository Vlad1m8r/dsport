package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.weu.dsport.domain.enums.ExerciseType;
import ru.weu.dsport.domain.enums.MuscleGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {

    @Schema(description = "Идентификатор упражнения", example = "1")
    private Long id;

    @Schema(description = "Название упражнения", example = "Приседания со штангой")
    private String name;

    @Schema(description = "Описание упражнения", example = "Приседания со штангой на плечах")
    private String description;

    @Schema(description = "Тип упражнения", example = "CUSTOM")
    private ExerciseType type;

    @Schema(description = "Мышечная группа", example = "LEGS")
    private MuscleGroup muscleGroup;
}
