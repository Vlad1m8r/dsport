package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetDto {

    @Schema(description = "Идентификатор подхода", example = "1")
    private Long id;

    @Schema(description = "Вес", example = "100.5")
    private Double weight;

    @Schema(description = "Количество повторений", example = "10")
    private Integer reps;
}
