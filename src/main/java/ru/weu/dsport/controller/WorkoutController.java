package ru.weu.dsport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
