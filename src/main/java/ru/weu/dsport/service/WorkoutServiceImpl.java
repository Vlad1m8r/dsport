package ru.weu.dsport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.weu.dsport.domain.*;
import ru.weu.dsport.exception.ResourceNotFoundException;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.WorkoutRepository;
import ru.weu.dsport.service.iface.WorkoutService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public Workout createWorkout(User user) {
        Workout workout = new Workout();
        workout.setUser(user);
        workout.setStartedAt(LocalDateTime.now());
        return workoutRepository.save(workout);
    }

    @Override
    @Transactional(readOnly = true)
    public Workout getWorkout(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Workout> getWorkouts(Long userId) {
        return workoutRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Workout finishWorkout(Long id) {
        Workout workout = getWorkout(id);
        workout.setEndedAt(LocalDateTime.now());
        return workoutRepository.save(workout);
    }

    @Override
    @Transactional
    public WorkoutExercise addExerciseToWorkout(Long workoutId, Long exerciseId) {
        Workout workout = getWorkout(workoutId);
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);

        workout.getWorkoutExercises().add(workoutExercise);
        return workoutExercise;
    }

    @Override
    @Transactional
    public WorkoutSet addSetToWorkoutExercise(Long workoutExerciseId, Double weight, Integer reps) {
        WorkoutExercise workoutExercise = workoutRepository.findWorkoutExerciseById(workoutExerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutExercise not found with id: " + workoutExerciseId));

        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkoutExercise(workoutExercise);
        workoutSet.setWeight(weight);
        workoutSet.setRepetitions(reps);

        workoutExercise.getWorkoutSets().add(workoutSet);
        return workoutSet;
    }
}
