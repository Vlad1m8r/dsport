package ru.weu.dsport.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.weu.dsport.domain.User;
import ru.weu.dsport.domain.Workout;
import ru.weu.dsport.domain.WorkoutExercise;
import ru.weu.dsport.domain.WorkoutSet;
import ru.weu.dsport.dto.WorkoutDto;
import ru.weu.dsport.dto.WorkoutExerciseDto;
import ru.weu.dsport.dto.WorkoutSetDto;
import ru.weu.dsport.dto.request.AddExerciseRequest;
import ru.weu.dsport.dto.request.AddSetRequest;
import ru.weu.dsport.mapper.WorkoutExerciseMapper;
import ru.weu.dsport.mapper.WorkoutMapper;
import ru.weu.dsport.mapper.WorkoutSetMapper;
import ru.weu.dsport.service.iface.UserService;
import ru.weu.dsport.service.iface.WorkoutService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final UserService userService;
    private final WorkoutMapper workoutMapper;
    private final WorkoutExerciseMapper workoutExerciseMapper;
    private final WorkoutSetMapper workoutSetMapper;

    @PostMapping
    public ResponseEntity<WorkoutDto> createWorkout(@RequestAttribute("userId") Long userId) {
        User user = userService.getUser(userId);
        Workout workout = workoutService.createWorkout(user);
        return ResponseEntity.ok(workoutMapper.toDto(workout));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDto> getWorkout(@PathVariable Long id) {
        Workout workout = workoutService.getWorkout(id);
        return ResponseEntity.ok(workoutMapper.toDto(workout));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutDto>> getWorkouts(@RequestAttribute("userId") Long userId) {
        List<Workout> workouts = workoutService.getWorkouts(userId);
        return ResponseEntity.ok(workoutMapper.toDto(workouts));
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<WorkoutDto> finishWorkout(@PathVariable Long id) {
        Workout workout = workoutService.finishWorkout(id);
        return ResponseEntity.ok(workoutMapper.toDto(workout));
    }

    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<WorkoutExerciseDto> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @Valid @RequestBody AddExerciseRequest request
    ) {
        WorkoutExercise workoutExercise = workoutService.addExerciseToWorkout(workoutId, request.getExerciseId());
        return ResponseEntity.ok(workoutExerciseMapper.toDto(workoutExercise));
    }

    @PostMapping("/exercises/{workoutExerciseId}/sets")
    public ResponseEntity<WorkoutSetDto> addSetToWorkoutExercise(
            @PathVariable Long workoutExerciseId,
            @Valid @RequestBody AddSetRequest request
    ) {
        WorkoutSet workoutSet = workoutService.addSetToWorkoutExercise(
                workoutExerciseId,
                request.getWeight(),
                request.getRepetitions()
        );
        return ResponseEntity.ok(workoutSetMapper.toDto(workoutSet));
    }
}
