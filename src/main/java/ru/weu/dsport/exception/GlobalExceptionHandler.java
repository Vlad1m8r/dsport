package ru.weu.dsport.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidInitDataException.class)
    public ResponseEntity<ApiError> handleInvalidInitData(
            InvalidInitDataException ex,
            HttpServletRequest request
    ) {
        ApiError response = ApiError.builder()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .path(request.getRequestURI())
                .code("UNAUTHORIZED")
                .message(ex.getMessage())
                .details(null)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        ApiError response = ApiError.builder()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .path(request.getRequestURI())
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .details(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        String code = ex.getCode() == null ? "BAD_REQUEST" : ex.getCode();
        ApiError response = ApiError.builder()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .path(request.getRequestURI())
                .code(code)
                .message(ex.getMessage())
                .details(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiError response = ApiError.builder()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .path(request.getRequestURI())
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
