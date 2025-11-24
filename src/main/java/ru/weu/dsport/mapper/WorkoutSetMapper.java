package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.weu.dsport.config.MapstructConfig;
import ru.weu.dsport.domain.WorkoutSet;
import ru.weu.dsport.dto.WorkoutSetDto;

@Mapper(config = MapstructConfig.class)
public interface WorkoutSetMapper extends BaseMapper<WorkoutSet, WorkoutSetDto> {

    WorkoutSetDto toDto(WorkoutSet workoutSet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "workoutExercise", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "durationSeconds", ignore = true)
    @Mapping(target = "rpe", ignore = true)
    WorkoutSet toEntity(WorkoutSetDto workoutSetDto);
}
