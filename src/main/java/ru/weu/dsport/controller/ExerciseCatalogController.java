package ru.weu.dsport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.weu.dsport.dto.ExerciseScope;
import ru.weu.dsport.dto.ExerciseSummaryResponse;
import ru.weu.dsport.dto.MuscleGroupCodeResponse;
import ru.weu.dsport.service.ExerciseCatalogService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExerciseCatalogController {

    private final ExerciseCatalogService exerciseCatalogService;

    @GetMapping("/muscle-groups")
    @Operation(summary = "Получить список групп мышц")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список групп мышц",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MuscleGroupCodeResponse.class))))
    })
    public List<MuscleGroupCodeResponse> listMuscleGroups() {
        return exerciseCatalogService.listMuscleGroups();
    }

    @GetMapping("/exercises")
    @Operation(summary = "Получить упражнения для picker")
    @Parameter(name = "query", in = ParameterIn.QUERY,
            description = "Поиск по названию упражнения (опционально)",
            schema = @Schema(type = "string", nullable = true))
    @Parameter(name = "muscleGroup", in = ParameterIn.QUERY,
            description = "Фильтр по группе мышц (опционально)",
            schema = @Schema(type = "string", nullable = true, example = "CHEST"))
    @Parameter(name = "scope", in = ParameterIn.QUERY,
            description = "Область поиска: ALL (по умолчанию), SYSTEM или MY",
            schema = @Schema(implementation = ExerciseScope.class, defaultValue = "ALL"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список упражнений",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ExerciseSummaryResponse.class))))
    })
    public List<ExerciseSummaryResponse> listExercises(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String muscleGroup,
            @RequestParam(required = false, defaultValue = "ALL") ExerciseScope scope
    ) {
        return exerciseCatalogService.listExercises(scope, query, muscleGroup);
    }
}
