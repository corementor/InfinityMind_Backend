package org.test.mindexpanseweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/math")
@Slf4j
public class AdditionController {

    private static final Logger logger = LoggerFactory.getLogger(AdditionController.class);
    private final Random random = new Random();

    // To store generated numbers for validation
    private final List<int[]> questions = new ArrayList<>();

    // Generate a single random question
    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers() {
        int number1 = random.nextInt(100); // Random number between 0-99
        int number2 = random.nextInt(100);

        return Map.of("number1", number1, "number2", number2);
    }


    // Verify multiple answers at once
    @PostMapping("/verify-all")
    public ResponseEntity<Map<String, Object>> verifyAllAnswers(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        List<String> results = new ArrayList<>();
        int correctCount = 0;

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            int correctAnswer = number1 + number2;

            if (correctAnswer == userAnswer) {
                results.add("Correct");
                correctCount++;
            } else {
                results.add("Incorrect. The correct answer is :" + correctAnswer);
            }
        }
            return ResponseEntity.ok(
                    Map.of(
                            "results", results,
                            "score", correctCount,
                            "total", userAnswerWithQuestions.size()
                    )
            );

    }
}
