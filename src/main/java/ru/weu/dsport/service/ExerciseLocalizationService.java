package ru.weu.dsport.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.ExerciseTranslation;
import ru.weu.dsport.domain.MuscleGroup;
import ru.weu.dsport.domain.MuscleGroupTranslation;
import ru.weu.dsport.repository.ExerciseTranslationRepository;
import ru.weu.dsport.repository.MuscleGroupTranslationRepository;

@Service
@RequiredArgsConstructor
public class ExerciseLocalizationService {

    public static final String DEFAULT_LANGUAGE_CODE = "en";

    private final ExerciseTranslationRepository exerciseTranslationRepository;
    private final MuscleGroupTranslationRepository muscleGroupTranslationRepository;

    public String normalizeLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) {
            return DEFAULT_LANGUAGE_CODE;
        }
        return languageCode.trim().toLowerCase(Locale.ROOT);
    }

    public Map<Long, LocalizedExerciseTranslation> resolveExerciseTranslations(
            Collection<Exercise> exercises,
            String preferredLanguageCode
    ) {
        if (exercises.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<ExerciseTranslation>> translationsByExerciseId = exerciseTranslationRepository.findByExerciseIds(
                        exercises.stream()
                                .map(Exercise::getId)
                                .toList()
                ).stream()
                .collect(Collectors.groupingBy(
                        translation -> translation.getExercise().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        String normalizedLanguageCode = normalizeLanguageCode(preferredLanguageCode);
        Map<Long, LocalizedExerciseTranslation> localizedTranslations = new LinkedHashMap<>();
        for (Exercise exercise : exercises) {
            ExerciseTranslation translation = selectTranslation(
                    translationsByExerciseId.get(exercise.getId()),
                    normalizedLanguageCode,
                    ExerciseTranslation::getLanguageCode
            );
            String resolvedName = translation != null ? translation.getName() : fallbackExerciseName(exercise.getId());
            localizedTranslations.put(
                    exercise.getId(),
                    new LocalizedExerciseTranslation(
                            resolvedName,
                            translation != null ? translation.getShortDescription() : null,
                            translation != null ? translation.getDescription() : null
                    )
            );
        }
        return localizedTranslations;
    }

    public Map<String, String> resolveMuscleGroupNames(
            Collection<MuscleGroup> muscleGroups,
            String preferredLanguageCode
    ) {
        if (muscleGroups.isEmpty()) {
            return Map.of();
        }

        Map<String, List<MuscleGroupTranslation>> translationsByCode = muscleGroupTranslationRepository.findByMuscleCodes(
                        muscleGroups.stream()
                                .map(MuscleGroup::getCode)
                                .toList()
                ).stream()
                .collect(Collectors.groupingBy(
                        translation -> translation.getMuscleGroup().getCode(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        String normalizedLanguageCode = normalizeLanguageCode(preferredLanguageCode);
        Map<String, String> localizedNames = new LinkedHashMap<>();
        for (MuscleGroup muscleGroup : muscleGroups) {
            MuscleGroupTranslation translation = selectTranslation(
                    translationsByCode.get(muscleGroup.getCode()),
                    normalizedLanguageCode,
                    MuscleGroupTranslation::getLanguageCode
            );
            localizedNames.put(
                    muscleGroup.getCode(),
                    translation != null ? translation.getName() : muscleGroup.getCode()
            );
        }
        return localizedNames;
    }

    private String fallbackExerciseName(Long exerciseId) {
        return "Exercise #" + exerciseId;
    }

    private <T> T selectTranslation(
            List<T> translations,
            String preferredLanguageCode,
            Function<T, String> languageExtractor
    ) {
        if (translations == null || translations.isEmpty()) {
            return null;
        }

        String normalizedLanguageCode = normalizeLanguageCode(preferredLanguageCode);
        for (T translation : translations) {
            if (normalizedLanguageCode.equals(normalizeLanguageCode(languageExtractor.apply(translation)))) {
                return translation;
            }
        }
        for (T translation : translations) {
            if (DEFAULT_LANGUAGE_CODE.equals(normalizeLanguageCode(languageExtractor.apply(translation)))) {
                return translation;
            }
        }
        return translations.getFirst();
    }

    public record LocalizedExerciseTranslation(
            String name,
            String shortDescription,
            String description
    ) {
    }
}
