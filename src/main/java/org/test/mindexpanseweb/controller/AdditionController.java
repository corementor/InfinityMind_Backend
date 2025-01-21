package org.test.mindexpanseweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@CrossOrigin(origins = "https://mind-expanse.vercel.app")
//@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/math/addition")
@Slf4j
public class AdditionController {
    private static final Logger logger = LoggerFactory.getLogger(AdditionController.class);
    private final Random random = new Random();

    /**
     * Generates a random number within a specified range
     */
    private int generateRandomNumber(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
    /**
     * Generates two numbers that when added together won't exceed 100
     */
    private Map<String, Integer> generateCompatibleNumbers(boolean singleDigit) {
        int number1, number2;
        if (singleDigit) {
            number1 = generateRandomNumber(0, 9);
            number2 = generateRandomNumber(0, 9 - number1); // Ensures sum ≤ 9
        } else {
            // For two-digit numbers, ensure their sum doesn't exceed 100
            number1 = generateRandomNumber(10, 89); // Max 89 to allow for at least 10 as number2
            int maxNumber2 = Math.min(99 - number1, 89); // Ensure number2 doesn't make sum exceed 100
            number2 = generateRandomNumber(10, maxNumber2);
        }
        return Map.of("number1", number1, "number2", number2);
    }

    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers(
            @RequestParam(required = false, defaultValue = "singleDigit") String type
    ) {
        boolean singleDigit = "singleDigit".equals(type);
        if ("fourDigit".equals(type)) {
            // Modified to potentially generate numbers that sum to 10000
            int number1 = random.nextInt(1000, 9000);
            int number2 = random.nextInt(1000, 9999); // Allow larger second number
            return Map.of("number1", number1, "number2", number2);
        }
        return generateCompatibleNumbers(singleDigit);
    }

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
                results.add("Incorrect. The correct answer is: " + correctAnswer);
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

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Tested successfully!!");
    }
}