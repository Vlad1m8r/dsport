package ru.weu.dsport.mapper;

import org.mapstruct.Mapper;
import ru.weu.dsport.domain.User;
import ru.weu.dsport.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
