package ru.weu.dsport.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.ExerciseType;
import ru.weu.dsport.domain.SetEntry;
import ru.weu.dsport.domain.WorkoutExercise;
import ru.weu.dsport.domain.WorkoutSession;
import ru.weu.dsport.dto.WorkoutSessionResponse;

class WorkoutMapperTest {

    private final WorkoutMapper mapper = new WorkoutMapper();

    @Test
    void toExerciseResponseIncludesExerciseNameAndType() {
        Exercise exerciseCatalogItem = Exercise.builder()
                .id(5L)
                .name("Планка")
                .type(ExerciseType.TIME)
                .build();
        SetEntry setEntry = SetEntry.builder()
                .id(11L)
                .orderIndex(1)
                .durationSeconds(45)
                .reps(null)
                .weight(null)
                .build();
        WorkoutExercise workoutExercise = WorkoutExercise.builder()
                .id(10L)
                .exercise(exerciseCatalogItem)
                .orderIndex(2)
                .setEntries(List.of(setEntry))
                .build();
        WorkoutSession session = WorkoutSession.builder()
                .id(100L)
                .title("2026-02-10 Планка")
                .startedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .exercises(List.of(workoutExercise))
                .build();

        WorkoutSessionResponse response = mapper.toResponse(session);

        assertThat(response.getExercises()).hasSize(1);
        WorkoutSessionResponse.WorkoutExerciseResponse mappedExercise = response.getExercises().getFirst();
        assertThat(mappedExercise.getExerciseId()).isEqualTo(5L);
        assertThat(mappedExercise.getExerciseName()).isEqualTo("Планка");
        assertThat(mappedExercise.getExerciseType()).isEqualTo(ExerciseType.TIME);
        assertThat(mappedExercise.getSets()).hasSize(1);
        assertThat(mappedExercise.getSets().getFirst().getDurationSeconds()).isEqualTo(45);
    }

    @Test
    void toSetResponseKeepsRepsWeightFieldsForStrengthExercise() {
        SetEntry setEntry = SetEntry.builder()
                .id(12L)
                .orderIndex(2)
                .reps(8)
                .weight(new BigDecimal("80.0"))
                .durationSeconds(null)
                .build();

        WorkoutSessionResponse.SetEntryResponse response = mapper.toSetResponse(setEntry);

        assertThat(response.getReps()).isEqualTo(8);
        assertThat(response.getWeight()).isEqualByComparingTo("80.0");
        assertThat(response.getDurationSeconds()).isNull();
    }
}
