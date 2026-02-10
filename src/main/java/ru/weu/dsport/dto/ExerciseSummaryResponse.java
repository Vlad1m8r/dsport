package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.weu.dsport.domain.ExerciseType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Краткая карточка упражнения для picker")
public class ExerciseSummaryResponse {

    @Schema(description = "Идентификатор упражнения", example = "1")
    private Long id;

    @Schema(description = "Название упражнения", example = "Bench Press")
    private String name;

    @Schema(description = "Тип упражнения", example = "REPS_WEIGHT")
    private ExerciseType type;

    @ArraySchema(schema = @Schema(description = "Код группы мышц", example = "CHEST"))
    private List<String> muscleGroups;

    @Schema(description = "Источник упражнения", example = "SYSTEM")
    private ExerciseOwnerScope scope;

    public enum ExerciseOwnerScope {
        SYSTEM,
        MY
    }
}
