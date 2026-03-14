package ru.weu.dsport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.ExerciseType;
import ru.weu.dsport.domain.MuscleGroup;
import ru.weu.dsport.dto.ExerciseScope;
import ru.weu.dsport.dto.ExerciseSummaryResponse;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.MuscleGroupRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseCatalogServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private MuscleGroupRepository muscleGroupRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private ExerciseLocalizationService exerciseLocalizationService;

    @Test
    void listExercisesUsesDefaultScopeAndAggregatesLocalizedMuscleGroups() {
        AppUser currentUser = user(42L, "en");
        Exercise benchPress = exercise(1L, ExerciseType.REPS_WEIGHT, null, Set.of(
                muscleGroup("CHEST"),
                muscleGroup("ARMS")
        ));
        Exercise myPullUp = exercise(2L, ExerciseType.REPS_WEIGHT, currentUser, Set.of(
                muscleGroup("BACK")
        ));

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(exerciseLocalizationService.normalizeLanguageCode("en")).thenReturn("en");
        when(exerciseRepository.findAvailableForUser(42L)).thenReturn(List.of(benchPress, myPullUp));
        when(exerciseLocalizationService.resolveExerciseTranslations(anyCollection(), eq("en"))).thenReturn(
                new LinkedHashMap<>(Map.of(
                        1L, new ExerciseLocalizationService.LocalizedExerciseTranslation("Bench Press", null, null),
                        2L, new ExerciseLocalizationService.LocalizedExerciseTranslation("My Pull Up", null, null)
                ))
        );
        when(exerciseLocalizationService.resolveMuscleGroupNames(anyCollection(), eq("en"))).thenReturn(
                Map.of(
                        "CHEST", "Chest",
                        "ARMS", "Arms",
                        "BACK", "Back"
                )
        );

        ExerciseCatalogService service = new ExerciseCatalogService(
                exerciseRepository,
                muscleGroupRepository,
                currentUserService,
                exerciseLocalizationService
        );

        List<ExerciseSummaryResponse> result = service.listExercises(null, " ", "");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getScope()).isEqualTo(ExerciseSummaryResponse.ExerciseOwnerScope.SYSTEM);
        assertThat(result.get(0).getMuscleGroups())
                .extracting("code")
                .containsExactly("ARMS", "CHEST");
        assertThat(result.get(0).getMuscleGroups())
                .extracting("name")
                .containsExactly("Arms", "Chest");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getScope()).isEqualTo(ExerciseSummaryResponse.ExerciseOwnerScope.MY);

        verify(exerciseRepository).findAvailableForUser(42L);
    }

    @Test
    void listExercisesAppliesLocalizedQueryAndMuscleGroupFiltersInService() {
        AppUser currentUser = user(7L, "en");
        Exercise benchPress = exercise(1L, ExerciseType.REPS_WEIGHT, null, Set.of(muscleGroup("CHEST")));
        Exercise pullUp = exercise(2L, ExerciseType.REPS_WEIGHT, currentUser, Set.of(muscleGroup("BACK")));

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(exerciseLocalizationService.normalizeLanguageCode("en")).thenReturn("en");
        when(exerciseRepository.findAvailableForUser(7L)).thenReturn(List.of(benchPress, pullUp));
        when(exerciseLocalizationService.resolveExerciseTranslations(anyCollection(), eq("en"))).thenReturn(
                Map.of(
                        1L, new ExerciseLocalizationService.LocalizedExerciseTranslation("Bench Press", null, null),
                        2L, new ExerciseLocalizationService.LocalizedExerciseTranslation("Pull Up", null, null)
                )
        );
        when(exerciseLocalizationService.resolveMuscleGroupNames(anyCollection(), eq("en"))).thenReturn(
                Map.of("CHEST", "Chest", "BACK", "Back")
        );

        ExerciseCatalogService service = new ExerciseCatalogService(
                exerciseRepository,
                muscleGroupRepository,
                currentUserService,
                exerciseLocalizationService
        );

        List<ExerciseSummaryResponse> result = service.listExercises(ExerciseScope.ALL, "  PrEsS ", " CHEST ");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        verify(exerciseRepository).findAvailableForUser(7L);
    }

    @Test
    void listExercisesFiltersBySystemScope() {
        AppUser currentUser = user(10L, "en");
        Exercise systemExercise = exercise(1L, ExerciseType.REPS_WEIGHT, null, Set.of());
        Exercise userExercise = exercise(2L, ExerciseType.TIME, currentUser, Set.of());

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(exerciseLocalizationService.normalizeLanguageCode("en")).thenReturn("en");
        when(exerciseRepository.findAvailableForUser(10L)).thenReturn(List.of(systemExercise, userExercise));
        when(exerciseLocalizationService.resolveExerciseTranslations(anyCollection(), eq("en"))).thenReturn(
                Map.of(
                        1L, new ExerciseLocalizationService.LocalizedExerciseTranslation("Bench Press", null, null),
                        2L, new ExerciseLocalizationService.LocalizedExerciseTranslation("My Timer", null, null)
                )
        );
        when(exerciseLocalizationService.resolveMuscleGroupNames(anyCollection(), eq("en"))).thenReturn(Map.of());

        ExerciseCatalogService service = new ExerciseCatalogService(
                exerciseRepository,
                muscleGroupRepository,
                currentUserService,
                exerciseLocalizationService
        );

        List<ExerciseSummaryResponse> result = service.listExercises(ExerciseScope.SYSTEM, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getScope()).isEqualTo(ExerciseSummaryResponse.ExerciseOwnerScope.SYSTEM);
    }

    @Test
    void listMuscleGroupsReturnsLocalizedNames() {
        AppUser currentUser = user(5L, "ru");
        List<MuscleGroup> muscleGroups = List.of(
                muscleGroup("BACK"),
                muscleGroup("CHEST")
        );

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(exerciseLocalizationService.normalizeLanguageCode("ru")).thenReturn("ru");
        when(muscleGroupRepository.findAllByOrderByCodeAsc()).thenReturn(muscleGroups);
        when(exerciseLocalizationService.resolveMuscleGroupNames(anyCollection(), eq("ru"))).thenReturn(
                Map.of(
                        "BACK", "Спина",
                        "CHEST", "Грудь"
                )
        );

        ExerciseCatalogService service = new ExerciseCatalogService(
                exerciseRepository,
                muscleGroupRepository,
                currentUserService,
                exerciseLocalizationService
        );

        assertThat(service.listMuscleGroups())
                .extracting("name")
                .containsExactly("Спина", "Грудь");
    }

    private AppUser user(Long id, String languageCode) {
        return AppUser.builder()
                .id(id)
                .tgUserId(id * 10)
                .languageCode(languageCode)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private Exercise exercise(Long id, ExerciseType type, AppUser ownerUser, Set<MuscleGroup> muscleGroups) {
        return Exercise.builder()
                .id(id)
                .type(type)
                .ownerUser(ownerUser)
                .muscleGroups(muscleGroups)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private MuscleGroup muscleGroup(String code) {
        return MuscleGroup.builder()
                .code(code)
                .build();
    }
}
