package ru.weu.dsport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import ru.weu.dsport.dto.GreetingResponse;

import java.util.List;

@Tag(name = "Greeting", description = "Endpoints for greeting visitors of the service.")
public interface HelloControllerApi {

    @GetMapping("/hello")
    @Operation(
            summary = "Get localized greeting",
            description = "Returns a localized greeting message to confirm the service is available.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Greeting returned successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GreetingResponse.class)))
            })
    ResponseEntity<GreetingResponse> getGreeting();

    @GetMapping("/data")
    @Operation(
            summary = "Get sample data array",
            description = "Returns a sample array of data objects for demonstration purposes.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Array returned successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = String.class))))
            })
    ResponseEntity<List<String>> getSampleData();
}
