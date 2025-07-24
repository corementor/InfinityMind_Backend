package io.corementor.mindexpanse.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
//@CrossOrigin(origins = "http://localhost:5173/")
@CrossOrigin(origins = "https://mind-expanse.vercel.app")

@RequestMapping("/api/v1/math/multiply")
@Slf4j
public class MultiplicationController {
    private static final Logger logger = LoggerFactory.getLogger(MultiplicationController.class);
    private final Random random = new Random();

    /**
     * Generates two numbers for a multiplication problem.
     *
     * @param type The type of numbers to generate ("singleDigit" or "multipleDigit").
     * @return A map containing the two numbers ("number1" and "number2").
     */
    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers(
            @RequestParam(required = false, defaultValue = "singleDigit") String type
    ) {
        boolean singleDigit = "singleDigit".equals(type);

        int number1 = singleDigit ? random.nextInt(10) : random.nextInt(100); // Number between 0-9 or 0-99
        int number2 = singleDigit ? random.nextInt(10) : random.nextInt(100); // Number between 0-9 or 0-99

        return Map.of("number1", number1, "number2", number2);
    }

    /**
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions A list of maps containing the user's answers and the corresponding questions.
     * @return A ResponseEntity containing the results, score, and total number of questions.
     */
    @PostMapping("/verify-all")
    public ResponseEntity<Map<String, Object>> verifyAllAnswers(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        List<String> results = new ArrayList<>();
        int correctCount = 0;

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            int correctAnswer = number1 * number2;

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


}