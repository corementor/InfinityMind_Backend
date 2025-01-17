package org.test.mindexpanseweb.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
//@CrossOrigin(origins = "https://mind-expanse.vercel.app/")
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/math/addition")

@Slf4j
public class AdditionController {
    private static final Logger logger = LoggerFactory.getLogger(AdditionController.class);
    private final Random random = new Random();


    @GetMapping("/generate")
    public Map<String, Integer> generateNumbers(
            @RequestParam(required = false, defaultValue = "singleDigit") String type
    ) {
//        logger.info("Generating random numbers for addition");
        boolean singleDigit = "singleDigit".equals(type);

        int number1 = singleDigit ? random.nextInt(10) : random.nextInt(100);
        int number2 = singleDigit ? random.nextInt(10) : random.nextInt(100);


        return Map.of("number1", number1, "number2", number2);
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


    @GetMapping("/test")
    public ResponseEntity<String>test(){
//        logger.info("Test method reached from addition controller");
        return ResponseEntity.ok("Tested successfully!!");
    }
}