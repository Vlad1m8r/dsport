package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на старт тренировки")
public class StartWorkoutRequest {

    @Schema(description = "Идентификатор шаблона", example = "10", nullable = true)
    private Long templateId;

    @Schema(description = "Заголовок тренировки", example = "2026-02-01 Грудь", nullable = true)
    private String title;
}
