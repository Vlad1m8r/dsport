package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import ru.weu.dsport.domain.WorkoutSet;
import ru.weu.dsport.dto.WorkoutSetDto;

@Mapper(componentModel = "spring")
public interface WorkoutSetMapper {
    WorkoutSetDto toDto(WorkoutSet workoutSet);
    WorkoutSet toEntity(WorkoutSetDto workoutSetDto);
}
