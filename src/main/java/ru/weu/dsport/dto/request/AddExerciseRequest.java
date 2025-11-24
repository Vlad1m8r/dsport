package ru.weu.dsport.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddExerciseRequest {
    @NotNull
    private Long exerciseId;
}
