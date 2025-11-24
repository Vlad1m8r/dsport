package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.weu.dsport.domain.enums.WorkoutStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDto {

    @Schema(description = "Идентификатор тренировки", example = "1")
    private Long id;

    @Schema(description = "Статус тренировки", example = "IN_PROGRESS")
    private WorkoutStatus status;

    @Schema(description = "Дата и время начала тренировки")
    private LocalDateTime startedAt;

    @Schema(description = "Дата и время окончания тренировки")
    private LocalDateTime finishedAt;

    @Schema(description = "Список упражнений в тренировке")
    private List<WorkoutExerciseDto> workoutExercises;
}
