package ru.weu.dsport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * A request to create a new workout session.
 * This request is intentionally empty. The user is identified via the
 * X-Telegram-User-ID header, and the start time is set on the server.
 */
@Data
@Schema(description = "Request to create a new workout session. The body is empty as the user is identified by a header.")
public class CreateWorkoutRequest {
}
