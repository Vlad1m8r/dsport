package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.weu.dsport.config.MapstructConfig;
import ru.weu.dsport.domain.User;
import ru.weu.dsport.dto.UserDto;

@Mapper(config = MapstructConfig.class)
public interface UserMapper extends BaseMapper<User, UserDto> {

    @Mapping(target = "lastName", ignore = true)
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentWeight", ignore = true)
    @Mapping(target = "height", ignore = true)
    User toEntity(UserDto userDto);
}
