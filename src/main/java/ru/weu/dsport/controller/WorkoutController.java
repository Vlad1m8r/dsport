package ru.weu.dsport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.weu.dsport.dto.AddSetEntryRequest;
import ru.weu.dsport.dto.AddWorkoutExerciseRequest;
import ru.weu.dsport.dto.StartWorkoutRequest;
import ru.weu.dsport.dto.WorkoutSessionResponse;
import ru.weu.dsport.exception.ApiError;
import ru.weu.dsport.service.WorkoutService;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping("/start")
    @Operation(summary = "Запустить тренировку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Тренировка создана",
                    content = @Content(schema = @Schema(implementation = WorkoutSessionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Шаблон не найден",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public WorkoutSessionResponse startWorkout(@Valid @RequestBody StartWorkoutRequest request) {
        return workoutService.startWorkout(request);
    }

    @PostMapping("/{workoutId}/exercises")
    @Operation(summary = "Добавить упражнение в тренировку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Упражнение добавлено",
                    content = @Content(schema = @Schema(
                            implementation = WorkoutSessionResponse.WorkoutExerciseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Тренировка или упражнение не найдены",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public WorkoutSessionResponse.WorkoutExerciseResponse addExercise(
            @PathVariable Long workoutId,
            @Valid @RequestBody AddWorkoutExerciseRequest request
    ) {
        return workoutService.addExercise(workoutId, request);
    }

    @DeleteMapping("/{workoutId}/exercises/{workoutExerciseId}")
    @Operation(summary = "Удалить упражнение из тренировки")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Упражнение удалено"),
            @ApiResponse(responseCode = "404", description = "Тренировка или упражнение не найдены",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteExercise(
            @PathVariable Long workoutId,
            @PathVariable Long workoutExerciseId
    ) {
        workoutService.deleteExercise(workoutId, workoutExerciseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workoutId}/exercises/{workoutExerciseId}/sets")
    @Operation(summary = "Добавить подход к упражнению")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Подход добавлен",
                    content = @Content(schema = @Schema(
                            implementation = WorkoutSessionResponse.SetEntryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Тренировка или упражнение не найдены",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public WorkoutSessionResponse.SetEntryResponse addSetEntry(
            @PathVariable Long workoutId,
            @PathVariable Long workoutExerciseId,
            @Valid @RequestBody AddSetEntryRequest request
    ) {
        return workoutService.addSetEntry(workoutId, workoutExerciseId, request);
    }

    @DeleteMapping("/{workoutId}/sets/{setEntryId}")
    @Operation(summary = "Удалить подход из тренировки")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Подход удален"),
            @ApiResponse(responseCode = "404", description = "Тренировка или подход не найдены",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteSetEntry(
            @PathVariable Long workoutId,
            @PathVariable Long setEntryId
    ) {
        workoutService.deleteSetEntry(workoutId, setEntryId);
        return ResponseEntity.noContent().build();
    }
}
