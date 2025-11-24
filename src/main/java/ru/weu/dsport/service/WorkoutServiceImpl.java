package ru.weu.dsport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.weu.dsport.domain.*;
import ru.weu.dsport.dto.WorkoutDto;
import ru.weu.dsport.exception.AccessDeniedException;
import ru.weu.dsport.exception.ResourceNotFoundException;
import ru.weu.dsport.mapper.WorkoutMapper;
import ru.weu.dsport.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutMapper workoutMapper;

    @Override
    @Transactional
    public WorkoutDto startWorkout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Workout workout = new Workout();
        workout.setUser(user);
        workout.setStartDt(LocalDateTime.now());
        return workoutMapper.toDto(workoutRepository.save(workout));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutDto> getAllWorkouts(Long userId) {
        return workoutRepository.findByUserId(userId).stream()
                .map(workoutMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutDto getWorkout(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + workoutId));
        if (!workout.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have access to this workout");
        }
        return workoutMapper.toDto(workout);
    }

    @Override
    @Transactional
    public WorkoutDto addExerciseToWorkout(Long workoutId, Long exerciseId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + workoutId));
        if (!workout.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have access to this workout");
        }
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExerciseRepository.save(workoutExercise);

        return workoutMapper.toDto(workoutRepository.findById(workoutId).get()); // Re-fetch to get all associations
    }

    @Override
    @Transactional
    public WorkoutDto addSetToWorkoutExercise(Long workoutExerciseId, Double weight, Integer reps, Long userId) {
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found with id: " + workoutExerciseId));
        if (!workoutExercise.getWorkout().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have access to this workout exercise");
        }

        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkoutExercise(workoutExercise);
        workoutSet.setWeight(weight);
        workoutSet.setReps(reps);
        workoutSetRepository.save(workoutSet);

        return workoutMapper.toDto(workoutRepository.findById(workoutExercise.getWorkout().getId()).get()); // Re-fetch to get all associations
    }
}
