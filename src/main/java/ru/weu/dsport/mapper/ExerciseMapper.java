package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.weu.dsport.config.MapstructConfig;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.dto.ExerciseDto;

@Mapper(config = MapstructConfig.class)
public interface ExerciseMapper extends BaseMapper<Exercise, ExerciseDto> {

    @Mapping(target = "type", source = "exerciseType")
    ExerciseDto toDto(Exercise exercise);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isSystem", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(source = "type", target = "exerciseType")
    Exercise toEntity(ExerciseDto exerciseDto);

}
