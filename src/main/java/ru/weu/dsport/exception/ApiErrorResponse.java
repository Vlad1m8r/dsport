package ru.weu.dsport.exception;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {

    private final OffsetDateTime timestamp;
    private final String path;
    private final String code;
    private final String message;
    private final Object details;
}
