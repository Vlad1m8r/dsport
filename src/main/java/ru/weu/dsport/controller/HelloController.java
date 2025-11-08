package ru.weu.dsport.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.weu.dsport.dto.GreetingResponse;
import ru.weu.dsport.dto.WorkoutResponse;

import java.util.List;
import java.time.LocalDate;

@RestController
public class HelloController implements HelloControllerApi {

    @Override
    public ResponseEntity<GreetingResponse> getGreeting() {
        return ResponseEntity.ok(new GreetingResponse("Привет, мир!"));
    }

    @Override
    public ResponseEntity<List<WorkoutResponse>> getSampleData() {
        List<WorkoutResponse> workouts = List.of(
                new WorkoutResponse("Кардио утро", LocalDate.of(2024, 3, 1), List.of("Бег на месте", "Берпи", "Прыжки на скакалке")),
                new WorkoutResponse("Силовая базовая", LocalDate.of(2024, 3, 3), List.of("Приседания", "Жим лёжа", "Тяга штанги в наклоне")),
                new WorkoutResponse("Йога баланс", LocalDate.of(2024, 3, 5), List.of("Поза воина", "Поза дерева", "Планка")),
                new WorkoutResponse("Функциональная с резинками", LocalDate.of(2024, 3, 7), List.of("Отведения ног", "Разгибания рук", "Скручивания")),
                new WorkoutResponse("Круговая интенсив", LocalDate.of(2024, 3, 9), List.of("Скакалка", "Бёрпи", "Отжимания")),
                new WorkoutResponse("Растяжка вечер", LocalDate.of(2024, 3, 11), List.of("Наклоны", "Растяжка спины", "Растяжка икроножных")),
                new WorkoutResponse("HIIT 20 минут", LocalDate.of(2024, 3, 13), List.of("Спринты", "Прыжки в ширину", "Скалывание коленей")),
                new WorkoutResponse("Силовой верх", LocalDate.of(2024, 3, 15), List.of("Подтягивания", "Отжимания на брусьях", "Тяга верхнего блока")),
                new WorkoutResponse("Нижняя часть", LocalDate.of(2024, 3, 17), List.of("Выпады", "Мёртвая тяга", "Жим ногами")),
                new WorkoutResponse("Плавание техника", LocalDate.of(2024, 3, 19), List.of("Кроль", "Брасс", "Спина"))
        );
        return ResponseEntity.ok(workouts);
    }
}
