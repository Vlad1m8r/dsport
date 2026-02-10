package ru.weu.dsport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.domain.ExerciseType;
import ru.weu.dsport.domain.MuscleGroup;
import ru.weu.dsport.dto.ExerciseScope;
import ru.weu.dsport.dto.ExerciseSummaryResponse;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.MuscleGroupRepository;
import ru.weu.dsport.repository.projection.ExerciseSummaryRow;

@ExtendWith(MockitoExtension.class)
class ExerciseCatalogServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private MuscleGroupRepository muscleGroupRepository;
    @Mock
    private CurrentUserService currentUserService;

    @Test
    void listExercisesUsesDefaultScopeAndAggregatesMuscleGroups() {
        AppUser currentUser = AppUser.builder()
                .id(42L)
                .tgUserId(420L)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(exerciseRepository.findSummaryRowsForPicker(42L, ExerciseScope.ALL, null, null)).thenReturn(List.of(
                new Row(1L, "Bench Press", ExerciseType.REPS_WEIGHT, "CHEST", true),
                new Row(1L, "Bench Press", ExerciseType.REPS_WEIGHT, "ARMS", true),
                new Row(2L, "My Pull Up", ExerciseType.REPS_WEIGHT, "BACK", false)
        ));

        ExerciseCatalogService service = new ExerciseCatalogService(
                exerciseRepository,
                muscleGroupRepository,
                currentUserService
        );

        List<ExerciseSummaryResponse> result = service.listExercises(null, " ", "");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getScope()).isEqualTo(ExerciseSummaryResponse.ExerciseOwnerScope.SYSTEM);
        assertThat(result.get(0).getMuscleGroups()).containsExactly("CHEST", "ARMS");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getScope()).isEqualTo(ExerciseSummaryResponse.ExerciseOwnerScope.MY);

        verify(exerciseRepository).findSummaryRowsForPicker(42L, ExerciseScope.ALL, null, null);
    }

    @Test
    void listExercisesPassesNormalizedFilters() {
        AppUser currentUser = AppUser.builder()
                .id(7L)
                .tgUserId(70L)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(exerciseRepository.findSummaryRowsForPicker(7L, ExerciseScope.MY, "press", "CHEST"))
                .thenReturn(List.of());

        ExerciseCatalogService service = new ExerciseCatalogService(
                exerciseRepository,
                muscleGroupRepository,
                currentUserService
        );
        service.listExercises(ExerciseScope.MY, "  press ", " CHEST ");

        verify(exerciseRepository).findSummaryRowsForPicker(7L, ExerciseScope.MY, "press", "CHEST");
    }

    @Test
    void listMuscleGroupsReturnsCodesOnly() {
        when(muscleGroupRepository.findAllByOrderByCodeAsc()).thenReturn(List.of(
                MuscleGroup.builder().code("BACK").name("Спина").build(),
                MuscleGroup.builder().code("CHEST").name("Грудь").build()
        ));

        ExerciseCatalogService service = new ExerciseCatalogService(
                exerciseRepository,
                muscleGroupRepository,
                currentUserService
        );

        assertThat(service.listMuscleGroups())
                .extracting("code")
                .containsExactly("BACK", "CHEST");
    }

    private static final class Row implements ExerciseSummaryRow {

        private final Long id;
        private final String name;
        private final ExerciseType type;
        private final String muscleCode;
        private final Boolean systemOwned;

        private Row(Long id, String name, ExerciseType type, String muscleCode, Boolean systemOwned) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.muscleCode = muscleCode;
            this.systemOwned = systemOwned;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ExerciseType getType() {
            return type;
        }

        @Override
        public String getMuscleCode() {
            return muscleCode;
        }

        @Override
        public Boolean getSystemOwned() {
            return systemOwned;
        }
    }
}
