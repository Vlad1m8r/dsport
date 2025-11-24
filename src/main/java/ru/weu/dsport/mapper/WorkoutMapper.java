package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import ru.weu.dsport.domain.Workout;
import ru.weu.dsport.dto.WorkoutDto;

@Mapper(componentModel = "spring", uses = {WorkoutExerciseMapper.class})
public interface WorkoutMapper {
    WorkoutDto toDto(Workout workout);
    Workout toEntity(WorkoutDto workoutDto);
}
