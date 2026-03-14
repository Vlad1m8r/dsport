package ru.weu.dsport.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.MuscleGroup;
import ru.weu.dsport.dto.ExerciseScope;
import ru.weu.dsport.dto.ExerciseSummaryResponse;
import ru.weu.dsport.dto.MuscleGroupCodeResponse;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.MuscleGroupRepository;

@Service
@RequiredArgsConstructor
public class ExerciseCatalogService {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;
    private final CurrentUserService currentUserService;
    private final ExerciseLocalizationService exerciseLocalizationService;

    public List<MuscleGroupCodeResponse> listMuscleGroups() {
        AppUser user = currentUserService.getCurrentUser();
        List<MuscleGroup> muscleGroups = muscleGroupRepository.findAllByOrderByCodeAsc();
        String languageCode = exerciseLocalizationService.normalizeLanguageCode(user.getLanguageCode());
        Map<String, String> localizedNames = exerciseLocalizationService.resolveMuscleGroupNames(
                muscleGroups,
                languageCode
        );

        return muscleGroups.stream()
                .map(muscleGroup -> MuscleGroupCodeResponse.builder()
                        .code(muscleGroup.getCode())
                        .name(localizedNames.getOrDefault(muscleGroup.getCode(), muscleGroup.getCode()))
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
        String normalizedQuery = normalizeQuery(query);
        String normalizedMuscleGroup = normalizeOptionalText(muscleGroup);
        String languageCode = exerciseLocalizationService.normalizeLanguageCode(user.getLanguageCode());

        List<Exercise> exercises = exerciseRepository.findAvailableForUser(user.getId());
        Map<Long, ExerciseLocalizationService.LocalizedExerciseTranslation> translations =
                exerciseLocalizationService.resolveExerciseTranslations(exercises, languageCode);
        Set<MuscleGroup> allMuscleGroups = exercises.stream()
                .flatMap(exercise -> exercise.getMuscleGroups().stream())
                .collect(Collectors.toSet());
        Map<String, String> localizedMuscleGroupNames = exerciseLocalizationService.resolveMuscleGroupNames(
                allMuscleGroups,
                languageCode
        );

        Predicate<Exercise> scopePredicate = switch (effectiveScope) {
            case ALL -> exercise -> true;
            case SYSTEM -> exercise -> exercise.getOwnerUser() == null;
            case MY -> exercise -> exercise.getOwnerUser() != null && user.getId().equals(exercise.getOwnerUser().getId());
        };

        return exercises.stream()
                .filter(scopePredicate)
                .filter(exercise -> matchesMuscleGroup(exercise, normalizedMuscleGroup))
                .filter(exercise -> matchesQuery(exercise, translations, normalizedQuery))
                .sorted((left, right) -> compareExercises(left, right, translations))
                .map(exercise -> toSummaryResponse(exercise, translations, localizedMuscleGroupNames))
                .toList();
    }

    private ExerciseSummaryResponse toSummaryResponse(
            Exercise exercise,
            Map<Long, ExerciseLocalizationService.LocalizedExerciseTranslation> translations,
            Map<String, String> localizedMuscleGroupNames
    ) {
        List<MuscleGroupCodeResponse> muscleGroups = exercise.getMuscleGroups().stream()
                .sorted(Comparator.comparing(MuscleGroup::getCode))
                .map(muscleGroup -> MuscleGroupCodeResponse.builder()
                        .code(muscleGroup.getCode())
                        .name(localizedMuscleGroupNames.getOrDefault(muscleGroup.getCode(), muscleGroup.getCode()))
                        .build())
                .toList();

        return ExerciseSummaryResponse.builder()
                .id(exercise.getId())
                .name(translations.get(exercise.getId()).name())
                .type(exercise.getType())
                .previewGifUrl(exercise.getPreviewGifUrl())
                .previewImageUrl(exercise.getPreviewImageUrl())
                .videoUrl(exercise.getVideoUrl())
                .muscleGroups(muscleGroups)
                .scope(exercise.getOwnerUser() == null
                        ? ExerciseSummaryResponse.ExerciseOwnerScope.SYSTEM
                        : ExerciseSummaryResponse.ExerciseOwnerScope.MY)
                .build();
    }

    private boolean matchesMuscleGroup(Exercise exercise, String muscleGroupCode) {
        if (muscleGroupCode == null) {
            return true;
        }
        return exercise.getMuscleGroups().stream()
                .anyMatch(group -> group.getCode().equals(muscleGroupCode));
    }

    private boolean matchesQuery(
            Exercise exercise,
            Map<Long, ExerciseLocalizationService.LocalizedExerciseTranslation> translations,
            String query
    ) {
        if (query == null) {
            return true;
        }
        String name = translations.get(exercise.getId()).name();
        return name.toLowerCase(Locale.ROOT).contains(query);
    }

    private int compareExercises(
            Exercise left,
            Exercise right,
            Map<Long, ExerciseLocalizationService.LocalizedExerciseTranslation> translations
    ) {
        int systemFirst = Boolean.compare(left.getOwnerUser() != null, right.getOwnerUser() != null);
        if (systemFirst != 0) {
            return systemFirst;
        }

        int localizedNameOrder = translations.get(left.getId()).name()
                .toLowerCase(Locale.ROOT)
                .compareTo(translations.get(right.getId()).name().toLowerCase(Locale.ROOT));
        if (localizedNameOrder != 0) {
            return localizedNameOrder;
        }

        return left.getId().compareTo(right.getId());
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeQuery(String value) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            return null;
        }
        return normalized.toLowerCase(Locale.ROOT);
    }
}
