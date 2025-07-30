package io.corementor.mindexpanse.controller;

import io.corementor.mindexpanse.service.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.corementor.mindexpanse.service.NumberGeneratorService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/math")
@Slf4j
@RequiredArgsConstructor
public class MathController {
    /**
     * The numberGeneratorService is used to generate random numbers
     */
    private final NumberGeneratorService numberGeneratorService;
    /**
     * The calculationService is used to calculate the  user's answers
     */
    private final CalculationService calculationService;

    /**
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions userAnswerWithQuestions
     * @return A ResponseEntity containing the results, score, and total number of questions
     */
    @PostMapping("/verify-all-with-carries")
    public ResponseEntity<Map<String, Object>> verifyAllAnswersWithCarries(
            @RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        Map<String, Object> results = calculationService.calculateSum(userAnswerWithQuestions);
        return ResponseEntity.ok(
                results
        );
    }

    /**
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions userAnswerWithQuestions
     * @return A ResponseEntity containing the results, score, and total number of questions
     */
    @PostMapping("/verify-subtractions")
    public ResponseEntity<Map<String, Object>> verifySubtractionsAnswers(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        Map<String, Object> results = calculationService.calculateSubtraction(userAnswerWithQuestions);
        return ResponseEntity.ok(
                results
        );
    }

    /**
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions userAnswerWithQuestions
     * @return A ResponseEntity containing the results, score, and total number of questions
     */

    @PostMapping("/verify-multiplication")
    public ResponseEntity<Map<String, Object>> verifyMultiplication(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        Map<String, Object> results = calculationService.calculateMultiplication(userAnswerWithQuestions);
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    /**
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions userAnswerWithQuestions
     * @return A ResponseEntity containing the results, score, and total number of questions
     */

    @PostMapping("/verify-division")
    public ResponseEntity<Map<String, Object>> verifyDivision(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        Map<String, Object> results = calculationService.calculateDivision(userAnswerWithQuestions);
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }


    /**
     * Generates a 2D array of random numbers within a specified range.
     *
     * @param rows The number of rows in the array.
     * @param cols The number of columns in the array.
     * @param min  The minimum value (inclusive) for each element.
     * @param max  The maximum value (inclusive) for each element.
     * @return A 2D array of integers.
     */

    @GetMapping("/generateArray")
    public int[][] generateNumbers(

            @RequestParam int rows,
            @RequestParam int cols,
            @RequestParam int min,
            @RequestParam int max
    ) {
        int[][] array = numberGeneratorService.generateRandom2DArray(rows, cols, min, max);
        numberGeneratorService.printArray(array);
        return numberGeneratorService.generateRandom2DArray(rows, cols, min, max);
    }


}