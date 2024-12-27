package org.test.mindexpanseweb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/math")
public class MathController {
    private final Random random = new Random();

    // To store generated numbers for validation
    private final List<int[]> questions = new ArrayList<>();

    // Generate a single random question
    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers() {
        int number1 = random.nextInt(100); // Random number between 0-99
        int number2 = random.nextInt(100);

        // Store the question
        questions.add(new int[]{number1, number2});

        return Map.of("number1", number1, "number2", number2);
    }

    // Verify a single answer
    @PostMapping("/verify")
    public ResponseEntity<String> verifyAnswer(@RequestBody Map<String, Integer> request) {
        int userAnswer = request.get("answer");
        int correctAnswer = questions.isEmpty() ? -1 : questions.get(0)[0] + questions.get(0)[1];

        if (userAnswer == correctAnswer) {
            return ResponseEntity.ok("Correct!");
        } else {
            return ResponseEntity.ok("Incorrect. The correct answer is " + correctAnswer);
        }
    }

    // Verify multiple answers at once
    @PostMapping("/verify-all")
    public ResponseEntity<Map<String, Object>> verifyAllAnswers(
            @RequestBody List<Map<String, Integer>> userAnswers) {

        List<String> results = new ArrayList<>();
        int correctCount = 0;

        for (int i = 0; i < userAnswers.size(); i++) {
            int userAnswer = userAnswers.get(i).get("answer");
            int correctAnswer = questions.get(i)[0] + questions.get(i)[1];

            if (userAnswer == correctAnswer) {
                results.add("Correct");
                correctCount++;
            } else {
                results.add("Incorrect. The correct answer is " + correctAnswer);
            }
        }

        // Reset questions after quiz to avoid reuse
        questions.clear();

        return ResponseEntity.ok(Map.of(
                "results", results,
                "score", correctCount,
                "total", userAnswers.size()
        ));
    }

    @GetMapping("/test")
    public String hello() {
        return "API WORKS";
    }
}
