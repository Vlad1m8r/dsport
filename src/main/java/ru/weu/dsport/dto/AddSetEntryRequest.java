package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление подхода к упражнению")
public class AddSetEntryRequest {

    @NotNull
    @PositiveOrZero
    @Schema(description = "Порядок подхода", example = "1")
    private Integer orderIndex;

    @Schema(description = "Фактические повторы", example = "12", nullable = true)
    private Integer reps;

    @Schema(description = "Вес", example = "80.0", nullable = true)
    private BigDecimal weight;

    @Schema(description = "Длительность в секундах", example = "60", nullable = true)
    private Integer durationSeconds;

    @AssertTrue(message = "reps или durationSeconds должны быть заданы")
    @Schema(hidden = true)
    public boolean isRepsOrDurationProvided() {
        return reps != null || durationSeconds != null;
    }
}
