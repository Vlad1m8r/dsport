package ru.weu.dsport.mapper;

import org.mapstruct.Mapping;
import ru.weu.dsport.domain.common.BaseEntity;
import ru.weu.dsport.dto.BaseDto;

import java.util.List;

public interface BaseMapper<E extends BaseEntity, D extends BaseDto> {

    D toDto(E entity);

    List<D> toDto(List<E> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    E toEntity(D dto);

    List<E> toEntities(List<D> dtos);
}
