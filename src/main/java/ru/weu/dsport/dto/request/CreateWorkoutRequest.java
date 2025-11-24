package ru.weu.dsport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkoutRequest {
    // For now, creating a workout doesn't require any specific fields
    // The user is identified by the X-Telegram-User-ID header
}
