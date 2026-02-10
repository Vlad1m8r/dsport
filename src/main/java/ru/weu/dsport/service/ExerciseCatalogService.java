package ru.weu.dsport.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.dto.ExerciseScope;
import ru.weu.dsport.dto.ExerciseSummaryResponse;
import ru.weu.dsport.dto.MuscleGroupCodeResponse;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.MuscleGroupRepository;
import ru.weu.dsport.repository.projection.ExerciseSummaryRow;

@Service
@RequiredArgsConstructor
public class ExerciseCatalogService {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;
    private final CurrentUserService currentUserService;

    public List<MuscleGroupCodeResponse> listMuscleGroups() {
        return muscleGroupRepository.findAllByOrderByCodeAsc().stream()
                .map(muscleGroup -> MuscleGroupCodeResponse.builder()
                        .code(muscleGroup.getCode())
                        .build())
                .toList();
    }

    public List<ExerciseSummaryResponse> listExercises(
            ExerciseScope scope,
            String query,
            String muscleGroup
    ) {
        AppUser user = currentUserService.getCurrentUser();
        ExerciseScope effectiveScope = scope == null ? ExerciseScope.ALL : scope;
        String scopeFilter = effectiveScope.name();
        String queryPattern = buildOptionalQueryPattern(query);
        String normalizedMuscleGroup = normalizeOptionalText(muscleGroup);

        List<ExerciseSummaryRow> rows = exerciseRepository.findSummaryRowsForPicker(
                user.getId(),
                scopeFilter,
                queryPattern,
                normalizedMuscleGroup
        );

        LinkedHashMap<Long, ExerciseSummaryAccumulator> grouped = new LinkedHashMap<>();
        for (ExerciseSummaryRow row : rows) {
            ExerciseSummaryAccumulator accumulator = grouped.computeIfAbsent(
                    row.getId(),
                    ignored -> new ExerciseSummaryAccumulator(
                            row.getId(),
                            row.getName(),
                            row.getType(),
                            Boolean.TRUE.equals(row.getSystemOwned())
                                    ? ExerciseSummaryResponse.ExerciseOwnerScope.SYSTEM
                                    : ExerciseSummaryResponse.ExerciseOwnerScope.MY
                    )
            );
            if (row.getMuscleCode() != null) {
                accumulator.muscleGroups().add(row.getMuscleCode());
            }
        }

        return grouped.values().stream()
                .map(item -> ExerciseSummaryResponse.builder()
                        .id(item.id())
                        .name(item.name())
                        .type(item.type())
                        .scope(item.scope())
                        .muscleGroups(item.muscleGroups())
                        .build())
                .toList();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String buildOptionalQueryPattern(String value) {
        String normalizedValue = normalizeOptionalText(value);
        if (normalizedValue == null) {
            return null;
        }
        return "%" + normalizedValue.toLowerCase(Locale.ROOT) + "%";
    }

    private record ExerciseSummaryAccumulator(
            Long id,
            String name,
            ru.weu.dsport.domain.ExerciseType type,
            ExerciseSummaryResponse.ExerciseOwnerScope scope,
            List<String> muscleGroups
    ) {

        private ExerciseSummaryAccumulator(
                Long id,
                String name,
                ru.weu.dsport.domain.ExerciseType type,
                ExerciseSummaryResponse.ExerciseOwnerScope scope
        ) {
            this(id, name, type, scope, new ArrayList<>());
        }
    }
}
