package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import ru.weu.dsport.domain.WorkoutExercise;
import ru.weu.dsport.dto.WorkoutExerciseDto;

@Mapper(componentModel = "spring", uses = {ExerciseMapper.class, WorkoutSetMapper.class})
public interface WorkoutExerciseMapper {
    WorkoutExerciseDto toDto(WorkoutExercise workoutExercise);
    WorkoutExercise toEntity(WorkoutExerciseDto workoutExerciseDto);
}
