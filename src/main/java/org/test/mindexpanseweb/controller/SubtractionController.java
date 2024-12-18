package org.test.mindexpanseweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/math/subtract")
@Slf4j
public class SubtractionController {
    private final Random random = new Random();

    // Generate a single random question
    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers() {
        // Ensure number1 is always larger than number2
        int number1 = random.nextInt(90) + 10; // Random number between 10-99
        int number2 = random.nextInt(number1); // Random number less than number1

        return Map.of("number1", number1, "number2", number2);
    }

    // Verify multiple answers at once
    @PostMapping("/verify-all")
    public ResponseEntity<Map<String, Object>> verifyAllAnswers(
            @RequestBody List<Map<String, Object>> userAnswersWithQuestions) {

        List<String> results = new ArrayList<>();
        int correctCount = 0;

        for (Map<String, Object> questionAnswer : userAnswersWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            int correctAnswer = number1 - number2;

            if (userAnswer == correctAnswer) {
                results.add("Correct");
                correctCount++;
            } else {
                results.add("Incorrect. The correct answer is " + correctAnswer);
            }
        }

        return ResponseEntity.ok(Map.of(
                "results", results,
                "score", correctCount,
                "total", userAnswersWithQuestions.size()
        ));
    }
}