package ru.weu.dsport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.weu.dsport.dto.TemplateCreateRequest;
import ru.weu.dsport.dto.TemplateResponse;
import ru.weu.dsport.exception.ApiError;
import ru.weu.dsport.service.TemplateService;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    @Operation(summary = "Создать шаблон тренировки")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Шаблон создан",
                    content = @Content(schema = @Schema(implementation = TemplateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TemplateResponse> create(@Valid @RequestBody TemplateCreateRequest request) {
        TemplateResponse response = templateService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить список шаблонов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список шаблонов",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = TemplateResponse.class))))
    })
    public List<TemplateResponse> list() {
        return templateService.listTemplates();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить шаблон")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Шаблон",
                    content = @Content(schema = @Schema(implementation = TemplateResponse.class))),
            @ApiResponse(responseCode = "404", description = "Шаблон не найден",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public TemplateResponse get(@PathVariable Long id) {
        return templateService.getTemplate(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить шаблон")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Шаблон обновлен",
                    content = @Content(schema = @Schema(implementation = TemplateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Шаблон не найден",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public TemplateResponse update(
            @PathVariable Long id,
            @Valid @RequestBody TemplateCreateRequest request
    ) {
        return templateService.updateTemplate(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить шаблон")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Шаблон удален"),
            @ApiResponse(responseCode = "404", description = "Шаблон не найден",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
