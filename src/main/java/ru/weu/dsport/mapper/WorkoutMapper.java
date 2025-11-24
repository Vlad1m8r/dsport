package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.weu.dsport.config.MapstructConfig;
import ru.weu.dsport.domain.Workout;
import ru.weu.dsport.dto.WorkoutDto;

@Mapper(config = MapstructConfig.class, uses = {WorkoutExerciseMapper.class})
public interface WorkoutMapper extends BaseMapper<Workout, WorkoutDto> {
    @Mapping(target = "startedAt", source = "startedAt")
    @Mapping(target = "finishedAt", source = "endedAt")
    WorkoutDto toDto(Workout workout);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "startedAt", target = "startedAt")
    @Mapping(source = "finishedAt", target = "endedAt")
    Workout toEntity(WorkoutDto workoutDto);
}
