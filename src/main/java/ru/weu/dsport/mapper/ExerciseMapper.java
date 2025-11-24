package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.dto.ExerciseDto;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    ExerciseDto toDto(Exercise exercise);
    Exercise toEntity(ExerciseDto exerciseDto);
}
