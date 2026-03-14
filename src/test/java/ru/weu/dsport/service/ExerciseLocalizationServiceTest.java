package ru.weu.dsport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.ExerciseTranslation;
import ru.weu.dsport.domain.MuscleGroup;
import ru.weu.dsport.domain.MuscleGroupTranslation;
import ru.weu.dsport.repository.ExerciseTranslationRepository;
import ru.weu.dsport.repository.MuscleGroupTranslationRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseLocalizationServiceTest {

    @Mock
    private ExerciseTranslationRepository exerciseTranslationRepository;
    @Mock
    private MuscleGroupTranslationRepository muscleGroupTranslationRepository;

    @Test
    void resolveExerciseTranslationsUsesPreferredLanguageThenEnglishThenAny() {
        Exercise systemExercise = Exercise.builder().id(1L).build();
        Exercise customExercise = Exercise.builder().id(2L).build();

        when(exerciseTranslationRepository.findByExerciseIds(List.of(1L, 2L))).thenReturn(List.of(
                ExerciseTranslation.builder()
                        .id(10L)
                        .exercise(systemExercise)
                        .languageCode("en")
                        .name("Bench Press")
                        .build(),
                ExerciseTranslation.builder()
                        .id(11L)
                        .exercise(systemExercise)
                        .languageCode("ru")
                        .name("Жим лёжа")
                        .build(),
                ExerciseTranslation.builder()
                        .id(20L)
                        .exercise(customExercise)
                        .languageCode("tr")
                        .name("Kendi hareketim")
                        .build()
        ));

        ExerciseLocalizationService service = new ExerciseLocalizationService(
                exerciseTranslationRepository,
                muscleGroupTranslationRepository
        );

        var result = service.resolveExerciseTranslations(List.of(systemExercise, customExercise), "ru");

        assertThat(result.get(1L).name()).isEqualTo("Жим лёжа");
        assertThat(result.get(2L).name()).isEqualTo("Kendi hareketim");
    }

    @Test
    void resolveMuscleGroupNamesFallsBackToEnglish() {
        MuscleGroup chest = MuscleGroup.builder().code("CHEST").build();
        MuscleGroup arms = MuscleGroup.builder().code("ARMS").build();

        when(muscleGroupTranslationRepository.findByMuscleCodes(List.of("CHEST", "ARMS"))).thenReturn(List.of(
                MuscleGroupTranslation.builder()
                        .id(1L)
                        .muscleGroup(chest)
                        .languageCode("en")
                        .name("Chest")
                        .build(),
                MuscleGroupTranslation.builder()
                        .id(2L)
                        .muscleGroup(arms)
                        .languageCode("tr")
                        .name("Kollar")
                        .build()
        ));

        ExerciseLocalizationService service = new ExerciseLocalizationService(
                exerciseTranslationRepository,
                muscleGroupTranslationRepository
        );

        var result = service.resolveMuscleGroupNames(List.of(chest, arms), "ru");

        assertThat(result.get("CHEST")).isEqualTo("Chest");
        assertThat(result.get("ARMS")).isEqualTo("Kollar");
    }
}
