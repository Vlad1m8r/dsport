package ru.weu.dsport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSetRequest {

    @NotNull
    @Min(0)
    @Schema(description = "Вес", example = "100.5")
    private Double weight;

    @NotNull
    @Min(1)
    @Schema(description = "Количество повторений", example = "10")
    private Integer reps;
}
