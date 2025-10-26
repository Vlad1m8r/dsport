package ru.weu.dsport.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.weu.dsport.dto.GreetingResponse;

import java.util.List;

@RestController
public class HelloController implements HelloControllerApi {

    @Override
    public ResponseEntity<GreetingResponse> getGreeting() {
        return ResponseEntity.ok(new GreetingResponse("Привет, мир!"));
    }

    @Override
    public ResponseEntity<List<String>> getSampleData() {
        return ResponseEntity.ok(List.of("alpha", "beta", "gamma"));
    }
}
