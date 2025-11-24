package ru.weu.dsport.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.weu.dsport.domain.common.BaseEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
@Table(name = "workout_sets")
public class WorkoutSet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private WorkoutExercise workoutExercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    private Double weight;

    private Integer reps;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    private Integer rpe;

}
