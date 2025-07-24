package io.corementor.mindexpanse.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
//@CrossOrigin("http://localhost:5173/")
@CrossOrigin(origins = "https://mind-expanse.vercel.app")

@RequestMapping("/api/v1/math/division")
@Slf4j
public class DivisionController {
    private static final Logger logger = LoggerFactory.getLogger(DivisionController.class);
    private final Random random = new Random();

    /**
     * Generates two numbers for a division problem.
     *
     * @param type The type of numbers to generate ("singleDigit" or "multipleDigit").
     * @return A map containing the dividend ("number1") and divisor ("number2").
     */
    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers(
            @RequestParam(required = false, defaultValue = "singleDigit") String type
    ) {
        boolean singleDigit = "singleDigit".equals(type);
        int divisor = random.nextInt(11) + 2; // Divisor between 2 and 12
        int dividend;

        if (singleDigit) {
            dividend = random.nextInt(11) + 2; // Dividend between 2 and 12 for single-digit
        } else {
            dividend = random.nextInt(988) + 12; // Dividend between 12 and 999 for multiple-digit
        }

        // Ensure that the dividend is greater than or equal to the divisor to avoid fractions
        if (dividend < divisor) {
            int temp = dividend;
            dividend = divisor;
            divisor = temp;
        }

        return Map.of("number1", dividend, "number2", divisor);
    }

    /**
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswersWithQuestions A list of maps containing the user's answers and the corresponding questions.
     * @return A ResponseEntity containing the results, score, and total number of questions.
     */
    @PostMapping("/verify-all")
    public ResponseEntity<Map<String, Object>> verifyAllAnswers(
            @RequestBody List<Map<String, Object>> userAnswersWithQuestions) {

        List<String> results = new ArrayList<>();
        int correctCount = 0;

        for (Map<String, Object> questionAnswer : userAnswersWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            int correctAnswer = number1 / number2;

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