package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.weu.dsport.domain.ExerciseType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с тренировочной сессией")
public class WorkoutSessionResponse {

    @Schema(description = "Идентификатор тренировки", example = "100")
    private Long id;

    @Schema(description = "Заголовок тренировки", example = "2026-02-01 Грудь")
    private String title;

    @Schema(description = "Дата и время старта")
    private OffsetDateTime startedAt;

    @Schema(description = "Дата и время завершения", nullable = true)
    private OffsetDateTime finishedAt;

    @Schema(description = "Идентификатор шаблона", example = "10", nullable = true)
    private Long templateId;

    @Schema(description = "Упражнения тренировки")
    private List<WorkoutExerciseResponse> exercises;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Упражнение в тренировке")
    public static class WorkoutExerciseResponse {

        @Schema(description = "Идентификатор записи упражнения", example = "1000")
        private Long id;

        @Schema(description = "Идентификатор упражнения", example = "1")
        private Long exerciseId;

        @Schema(description = "Название упражнения", example = "Жим штанги лёжа")
        private String exerciseName;

        @Schema(
                description = "Тип упражнения. REPS_WEIGHT — силовое (reps/weight), TIME — на время (durationSeconds)",
                example = "REPS_WEIGHT"
        )
        private ExerciseType exerciseType;

        @Schema(description = "Порядок упражнения", example = "1")
        private int orderIndex;

        @Schema(description = "Подходы")
        private List<SetEntryResponse> sets;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Фактический подход")
    public static class SetEntryResponse {

        @Schema(description = "Идентификатор подхода", example = "5000")
        private Long id;

        @Schema(description = "Порядок подхода", example = "1")
        private int orderIndex;

        @Schema(description = "Фактические повторы (для REPS_WEIGHT)", example = "10", nullable = true)
        private Integer reps;

        @Schema(description = "Вес (для REPS_WEIGHT)", example = "50.0", nullable = true)
        private BigDecimal weight;

        @Schema(description = "Длительность в секундах (для TIME)", example = "60", nullable = true)
        private Integer durationSeconds;
    }
}
