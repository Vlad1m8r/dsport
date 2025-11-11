package ru.weu.dsport.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.weu.dsport.dto.WorkoutResponse;

class WorkoutServiceTest {

    @Test
    void shouldReturnOnlyWorkoutsFromCurrentMonth() {
        ZoneId zone = ZoneId.of("UTC");
        LocalDate currentDate = LocalDate.of(2024, 3, 15);
        Clock fixedClock = Clock.fixed(currentDate.atStartOfDay(zone).toInstant(), zone);
        WorkoutService workoutService = new WorkoutService(fixedClock);

        List<WorkoutResponse> workouts = workoutService.getCurrentMonthWorkouts();

        assertThat(workouts)
                .hasSize(3)
                .allSatisfy(workout -> assertThat(YearMonth.from(workout.getDate())).isEqualTo(YearMonth.from(currentDate)));
    }
}

