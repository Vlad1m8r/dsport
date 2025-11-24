package ru.weu.dsport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.weu.dsport.dto.WorkoutDto;
import ru.weu.dsport.dto.request.AddExerciseRequest;
import ru.weu.dsport.dto.request.AddSetRequest;
import ru.weu.dsport.dto.request.CreateWorkoutRequest;
import ru.weu.dsport.service.WorkoutService;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "Workout management APIs")
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    @Operation(summary = "Start a new workout session")
    @ApiResponse(responseCode = "200", description = "Workout created successfully",
            content = @Content(schema = @Schema(implementation = WorkoutDto.class)))
    public ResponseEntity<WorkoutDto> startWorkout(
            @Parameter(hidden = true) @RequestHeader("X-Telegram-User-Id") Long userId,
            @RequestBody @Valid CreateWorkoutRequest request) {
        return ResponseEntity.ok(workoutService.startWorkout(userId));
    }

    @GetMapping
    @Operation(summary = "Get all workouts for the current user")
    public ResponseEntity<List<WorkoutDto>> getAllWorkouts(
            @Parameter(hidden = true) @RequestHeader("X-Telegram-User-Id") Long userId) {
        return ResponseEntity.ok(workoutService.getAllWorkouts(userId));
    }

    @GetMapping("/{workoutId}")
    @Operation(summary = "Get a specific workout by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = WorkoutDto.class)))
    @ApiResponse(responseCode = "404", description = "Workout not found")
    public ResponseEntity<WorkoutDto> getWorkout(
            @Parameter(hidden = true) @RequestHeader("X-Telegram-User-Id") Long userId,
            @PathVariable Long workoutId) {
        return ResponseEntity.ok(workoutService.getWorkout(workoutId, userId));
    }

    @PostMapping("/{workoutId}/exercises")
    @Operation(summary = "Add an exercise to a workout")
    @ApiResponse(responseCode = "200", description = "Exercise added successfully",
            content = @Content(schema = @Schema(implementation = WorkoutDto.class)))
    public ResponseEntity<WorkoutDto> addExerciseToWorkout(
            @Parameter(hidden = true) @RequestHeader("X-Telegram-User-Id") Long userId,
            @PathVariable Long workoutId,
            @RequestBody @Valid AddExerciseRequest request) {
        return ResponseEntity.ok(workoutService.addExerciseToWorkout(workoutId, request.getExerciseId(), userId));
    }

    @PostMapping("/exercises/{workoutExerciseId}/sets")
    @Operation(summary = "Add a set to a workout exercise")
    @ApiResponse(responseCode = "200", description = "Set added successfully",
            content = @Content(schema = @Schema(implementation = WorkoutDto.class)))
    public ResponseEntity<WorkoutDto> addSetToWorkoutExercise(
            @Parameter(hidden = true) @RequestHeader("X-Telegram-User-Id") Long userId,
            @PathVariable Long workoutExerciseId,
            @RequestBody @Valid AddSetRequest request) {
        return ResponseEntity.ok(workoutService.addSetToWorkoutExercise(workoutExerciseId, request.getWeight(), request.getReps(), userId));
    }
}
