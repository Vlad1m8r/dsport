package ru.weu.dsport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddExerciseRequest {

    @NotNull
    @Schema(description = "Идентификатор упражнения", example = "1")
    private Long exerciseId;
}
