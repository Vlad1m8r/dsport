package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Краткая информация о тренировке")
public class WorkoutSummaryResponse {

    @Schema(description = "Идентификатор тренировки", example = "1")
    private Long id;

    @Schema(description = "Заголовок тренировки", example = "2026-02-01 Грудь")
    private String title;

    @Schema(description = "Дата и время старта")
    private OffsetDateTime startedAt;

    @Schema(description = "Дата и время завершения", nullable = true)
    private OffsetDateTime finishedAt;

    @Schema(description = "Идентификатор шаблона", example = "10", nullable = true)
    private Long templateId;
}
