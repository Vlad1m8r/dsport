package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Идентификатор пользователя в Telegram", example = "123456789")
    private Long telegramId;

    @Schema(description = "Имя пользователя", example = "John Doe")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Smith")
    private String lastName;

    @Schema(description = "Имя пользователя в Telegram", example = "john_doe")
    private String username;
}
