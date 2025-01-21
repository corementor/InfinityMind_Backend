package org.test.mindexpanseweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


@RestController
//@CrossOrigin("http://localhost:5173/")
@CrossOrigin(origins = "https://mind-expanse.vercel.app")
@RequestMapping("/api/math/division")
@Slf4j
public class DivisionController {
    private static final Logger logger = LoggerFactory.getLogger(SubtractionController.class);
    private final Random random = new Random();


    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers(
            @RequestParam(required = false, defaultValue = "singleDigit") String type
    ) {
        boolean singleDigit = "singleDigit".equals(type);
        int number1, number2;
        do {
            number1 = singleDigit ? random.nextInt(9) + 1 : random.nextInt(90) + 10;
            number2 = random.nextInt(number1);
        } while (number2 == 0);
        return Map.of("number1", number1, "number2", number2);
    }

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
