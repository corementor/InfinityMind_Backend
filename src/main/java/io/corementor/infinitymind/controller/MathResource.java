package io.corementor.infinitymind.controller;

import io.corementor.infinitymind.dto.DivisionQuestion;
import io.corementor.infinitymind.service.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.corementor.infinitymind.service.NumberGeneratorService;

import java.util.*;

/**
 * The class Math Controller.
 *
 * @author Blaise Mugisha
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/math")
@Slf4j
@RequiredArgsConstructor
public class MathResource {
    /**
     * The numberGeneratorService is used to generate random numbers
     */
    private final NumberGeneratorService numberGeneratorService;
    /**
     * The calculationService is used to calculate the  user's answers
     */
    private final CalculationService calculationService;


    /**
     * Generate Numbers.
     * <p>
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
        log.info("Generate Array endpoint :: reached");
        int[][] array = numberGeneratorService.generateRandom2DArray(rows, cols, min, max);
        numberGeneratorService.printArray(array);
        return numberGeneratorService.generateRandom2DArray(rows, cols, min, max);
    }

    /**
     * Verify Addition.
     * Verifies the user's answers against the correct answers,correct carries  and returns the results.
     *
     * @param userAnswerWithQuestions userAnswerWithQuestions
     * @return A ResponseEntity containing the results, score,percentage,max score,totalQuestions, correctCarries, and carry validations
     */
    @PostMapping("/verify-additions")
    public ResponseEntity<Map<String, Object>> verifyAddition(
            @RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        log.info("Addition endpoint :: reached");
        Map<String, Object> results = calculationService.calculateSum(userAnswerWithQuestions);
        return ResponseEntity.ok(
                results
        );
    }

    /**
     * Verify Subtraction Answers.
     * <p>
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions userAnswerWithQuestions
     * @return A ResponseEntity containing the results, score, percentage,maxScore,total questions,correct borrows and borrow validations.
     */
    @PostMapping("/verify-subtractions")
    public ResponseEntity<Map<String, Object>> verifySubtractionsAnswers(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
       log.info("Subtraction endpoint :: reached");
        Map<String, Object> results = calculationService.calculateSubtraction(userAnswerWithQuestions);
        return ResponseEntity.ok(
                results
        );
    }

    /**
     * Verify Multiplication.
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions userAnswerWithQuestions
     * @return A ResponseEntity containing the results, score,percentage,maxScore,total questions,correct carries,correct partial products and carry validations
     */

    @PostMapping("/verify-multiplications")
    public ResponseEntity<Map<String, Object>> verifyMultiplication(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        log.info("Multiplication endpoint :: reached");
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
     * generateDivisionQuestion
     *
     * @param count count
     * @param min   min
     * @param max   max
     * @return List
     */
    @GetMapping("/generate-division")
    public List<DivisionQuestion> generateDivisionQuestions(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "1") int min,
            @RequestParam(defaultValue = "1000") int max
    ) {
        List<DivisionQuestion> array = numberGeneratorService.generateDivisionQuestions(count, min, max);
        return numberGeneratorService.generateDivisionQuestions(count, min, max);
    }

}