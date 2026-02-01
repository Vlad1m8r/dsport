package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание или обновление шаблона тренировки")
public class TemplateCreateRequest {

    @NotBlank
    @Schema(description = "Название шаблона", example = "Грудь")
    private String name;

    @Valid
    @NotNull
    @Schema(description = "Упражнения в шаблоне")
    private List<TemplateExerciseRequest> exercises;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Упражнение в шаблоне")
    public static class TemplateExerciseRequest {

        @NotNull
        @Schema(description = "Идентификатор упражнения", example = "1")
        private Long exerciseId;

        @Min(0)
        @Schema(description = "Порядок упражнения", example = "1")
        private int orderIndex;

        @Valid
        @NotNull
        @Schema(description = "Плановые подходы")
        private List<TemplateSetRequest> sets;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Плановый подход")
    public static class TemplateSetRequest {

        @Min(0)
        @Schema(description = "Порядок подхода", example = "1")
        private int orderIndex;

        @Min(1)
        @Schema(description = "Плановые повторы", example = "10", nullable = true)
        private Integer plannedReps;

        @Min(1)
        @Schema(description = "Плановая длительность в секундах", example = "60", nullable = true)
        private Integer plannedDurationSeconds;

        @AssertTrue(message = "Должно быть задано ровно одно из plannedReps или plannedDurationSeconds")
        @Schema(description = "Проверка корректности набора полей")
        public boolean isOneOfPlannedFieldsPresent() {
            return (plannedReps != null) ^ (plannedDurationSeconds != null);
        }
    }
}
