package ru.weu.dsport.repository.projection;

import ru.weu.dsport.domain.ExerciseType;

public interface ExerciseSummaryRow {

    Long getId();

    String getName();

    ExerciseType getType();

    String getMuscleCode();

    Boolean getSystemOwned();
}
