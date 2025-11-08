package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Информация о тренировке с перечнем упражнений")
public class WorkoutResponse {

    @Schema(description = "Название тренировки", example = "Кардио утро")
    private final String title;

    @Schema(description = "Дата проведения тренировки", example = "2024-04-01")
    private final LocalDate date;

    @Schema(description = "Список упражнений, входящих в тренировку", example = "[Берпи, Планка, Бег на месте]")
    private final List<String> exercises;

    public WorkoutResponse(String title, LocalDate date, List<String> exercises) {
        this.title = title;
        this.date = date;
        this.exercises = exercises;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<String> getExercises() {
        return exercises;
    }
}
