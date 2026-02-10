package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Область поиска упражнений")
public enum ExerciseScope {
    ALL,
    SYSTEM,
    MY
}
