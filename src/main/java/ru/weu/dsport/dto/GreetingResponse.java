package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GreetingResponse", description = "Greeting payload that contains a localized welcome message.")
public record GreetingResponse(
        @Schema(description = "Localized greeting message to show to the caller.", example = "Привет, мир!")
        String message) {
}
