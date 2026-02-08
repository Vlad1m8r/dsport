package ru.weu.dsport.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление подхода")
@JsonDeserialize(using = UpdateSetEntryRequestDeserializer.class)
public class UpdateSetEntryRequest {

    @Schema(description = "Порядок подхода", example = "1", nullable = true)
    private Integer orderIndex;

    @Schema(description = "Фактические повторы", example = "10", nullable = true)
    private Integer reps;

    @Schema(description = "Вес", example = "70.0", nullable = true)
    private BigDecimal weight;

    @Schema(description = "Длительность в секундах", example = "45", nullable = true)
    private Integer durationSeconds;

    @JsonIgnore
    private boolean orderIndexProvided;

    @JsonIgnore
    private boolean repsProvided;

    @JsonIgnore
    private boolean weightProvided;

    @JsonIgnore
    private boolean durationSecondsProvided;

    @AssertTrue(message = "orderIndex должен быть >= 1")
    @Schema(hidden = true)
    public boolean isOrderIndexValid() {
        if (!orderIndexProvided) {
            return true;
        }
        return orderIndex != null && orderIndex >= 1;
    }

    @AssertTrue(message = "reps должен быть >= 0")
    @Schema(hidden = true)
    public boolean isRepsValid() {
        if (!repsProvided) {
            return true;
        }
        return reps == null || reps >= 0;
    }

    @AssertTrue(message = "durationSeconds должен быть >= 0")
    @Schema(hidden = true)
    public boolean isDurationSecondsValid() {
        if (!durationSecondsProvided) {
            return true;
        }
        return durationSeconds == null || durationSeconds >= 0;
    }
}
