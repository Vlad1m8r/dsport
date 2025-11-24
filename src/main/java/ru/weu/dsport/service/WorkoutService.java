package ru.weu.dsport.service;

import ru.weu.dsport.dto.WorkoutDto;

import java.util.List;

public interface WorkoutService {

    WorkoutDto startWorkout(Long userId);

    List<WorkoutDto> getAllWorkouts(Long userId);

    WorkoutDto getWorkout(Long workoutId, Long userId);

    WorkoutDto addExerciseToWorkout(Long workoutId, Long exerciseId, Long userId);

    WorkoutDto addSetToWorkoutExercise(Long workoutExerciseId, Double weight, Integer reps, Long userId);
}
