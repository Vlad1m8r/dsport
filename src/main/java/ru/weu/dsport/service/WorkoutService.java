package ru.weu.dsport.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import ru.weu.dsport.dto.WorkoutResponse;

@Service
public class WorkoutService {

    private final Clock clock;

    public WorkoutService() {
        this(Clock.systemDefaultZone());
    }

    public WorkoutService(Clock clock) {
        this.clock = clock;
    }

    public List<WorkoutResponse> getCurrentMonthWorkouts() {
        LocalDate today = LocalDate.now(clock);
        YearMonth currentMonth = YearMonth.from(today);

        return loadMockWorkouts(today).stream()
                .filter(workout -> YearMonth.from(workout.getDate()).equals(currentMonth))
                .toList();
    }

    private List<WorkoutResponse> loadMockWorkouts(LocalDate today) {
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth previousMonth = currentMonth.minusMonths(1);
        YearMonth nextMonth = currentMonth.plusMonths(1);

        return List.of(
                new WorkoutResponse(
                        "Кардио утро",
                        currentMonth.atDay(1),
                        List.of("Бег на месте", "Берпи", "Прыжки на скакалке")),
                new WorkoutResponse(
                        "Силовая базовая",
                        currentMonth.atDay(Math.min(3, currentMonth.lengthOfMonth())),
                        List.of("Приседания", "Жим лёжа", "Тяга штанги в наклоне")),
                new WorkoutResponse(
                        "Йога баланс",
                        currentMonth.atDay(Math.min(5, currentMonth.lengthOfMonth())),
                        List.of("Поза воина", "Поза дерева", "Планка")),
                new WorkoutResponse(
                        "Подготовка к марафону",
                        previousMonth.atDay(Math.min(10, previousMonth.lengthOfMonth())),
                        List.of("Бег", "Интервалы", "Силовая подготовка")),
                new WorkoutResponse(
                        "Восстановительная плавная",
                        nextMonth.atDay(Math.min(8, nextMonth.lengthOfMonth())),
                        List.of("Растяжка", "Лёгкий бег", "Плавание"))
        );
    }
}

