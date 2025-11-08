package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Информация о тренировке с перечнем упражнений")
public record WorkoutResponse(
        @Schema(description = "Название тренировки", example = "Кардио утро")
        String title,
        @Schema(description = "Дата проведения тренировки", example = "2024-04-01")
        LocalDate date,
        @Schema(description = "Список упражнений, входящих в тренировку", type = "array",
                example = "[\\"Берпи\\", \\"Планка\\", \\"Бег на месте\\"]")
        List<String> exercises
) {
}
