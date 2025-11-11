package ru.weu.dsport.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ru.weu.dsport.dto.GreetingResponse;
import ru.weu.dsport.dto.WorkoutResponse;
import ru.weu.dsport.service.WorkoutService;

@RestController
@RequiredArgsConstructor
public class HelloController implements HelloControllerApi {

    private final WorkoutService workoutService;

    @Override
    public ResponseEntity<GreetingResponse> getGreeting() {
        return ResponseEntity.ok(new GreetingResponse("Привет, мир!"));
    }

    @Override
    public ResponseEntity<List<WorkoutResponse>> getSampleData() {
        return ResponseEntity.ok(workoutService.getCurrentMonthWorkouts());
    }
}
