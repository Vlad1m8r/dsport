package ru.weu.dsport.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Ошибка API")
public class ApiError {

    @Schema(description = "Время ошибки")
    private final OffsetDateTime timestamp;

    @Schema(description = "Путь запроса")
    private final String path;

    @Schema(description = "Код ошибки", example = "NOT_FOUND")
    private final String code;

    @Schema(description = "Сообщение")
    private final String message;

    @Schema(description = "Дополнительные детали", nullable = true)
    private final Object details;

    @Schema(description = "Дополнительные данные ошибки", nullable = true)
    private final Object data;
}
