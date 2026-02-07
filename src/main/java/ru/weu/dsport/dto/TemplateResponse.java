package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с шаблоном тренировки")
public class TemplateResponse {

    @Schema(description = "Идентификатор шаблона", example = "10")
    private Long id;

    @Schema(description = "Название шаблона", example = "Грудь")
    private String name;

    @Schema(description = "Упражнения в шаблоне")
    private List<TemplateExerciseResponse> exercises;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Упражнение в шаблоне")
    public static class TemplateExerciseResponse {

        @Schema(description = "Идентификатор упражнения", example = "1")
        private Long exerciseId;

        @Schema(description = "Порядок упражнения", example = "1")
        private int orderIndex;

        @Schema(description = "Плановые подходы")
        private List<TemplateSetResponse> sets;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Плановый подход")
    public static class TemplateSetResponse {

        @Schema(description = "Порядок подхода", example = "1")
        private int orderIndex;

        @Schema(description = "Плановые повторы", example = "10", nullable = true)
        private Integer plannedReps;

        @Schema(description = "Плановая длительность в секундах", example = "60", nullable = true)
        private Integer plannedDurationSeconds;
    }
}
