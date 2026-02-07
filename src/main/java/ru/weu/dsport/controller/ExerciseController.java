package ru.weu.dsport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.weu.dsport.dto.ExerciseLastMaxResponse;
import ru.weu.dsport.exception.ApiError;
import ru.weu.dsport.service.ExerciseStatsService;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseStatsService exerciseStatsService;

    @GetMapping("/{exerciseId}/last-max")
    @Operation(summary = "Получить последний максимум по упражнению")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Последний максимум",
                    content = @Content(schema = @Schema(implementation = ExerciseLastMaxResponse.class))),
            @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ExerciseLastMaxResponse getLastMax(@PathVariable Long exerciseId) {
        return exerciseStatsService.getLastMax(exerciseId);
    }
}
