package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.weu.dsport.config.MapstructConfig;
import ru.weu.dsport.domain.WorkoutExercise;
import ru.weu.dsport.dto.WorkoutExerciseDto;

@Mapper(config = MapstructConfig.class, uses = {WorkoutSetMapper.class, ExerciseMapper.class})
public interface WorkoutExerciseMapper extends BaseMapper<WorkoutExercise, WorkoutExerciseDto> {

    WorkoutExerciseDto toDto(WorkoutExercise workoutExercise);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "workout", ignore = true)
    WorkoutExercise toEntity(WorkoutExerciseDto workoutExerciseDto);
}
