package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с последним максимумом по упражнению")
public class ExerciseLastMaxResponse {

    @Schema(description = "Идентификатор упражнения", example = "123")
    private Long exerciseId;

    @Schema(description = "Идентификатор последней тренировки", example = "456", nullable = true)
    private Long lastWorkoutId;

    @Schema(description = "Дата и время старта последней тренировки", nullable = true)
    private OffsetDateTime lastWorkoutStartedAt;

    @Schema(description = "Максимальный вес в последней тренировке", example = "80.0", nullable = true)
    private BigDecimal maxWeight;

    @Schema(description = "Максимальная длительность в секундах в последней тренировке", example = "120",
            nullable = true)
    private Integer maxDurationSeconds;
}
