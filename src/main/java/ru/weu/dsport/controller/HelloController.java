package ru.weu.dsport.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> getGreeting() {
        return ResponseEntity.ok(Map.of("message", "Привет, мир!"));
    }
}
