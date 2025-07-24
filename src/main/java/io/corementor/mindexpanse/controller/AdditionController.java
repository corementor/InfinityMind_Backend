package io.corementor.mindexpanse.controller;

import io.corementor.mindexpanse.service.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.corementor.mindexpanse.service.NumberGeneratorService;

import java.util.*;

@RestController
@CrossOrigin(origins = "https://mind-expanse.vercel.app")
//@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/v1/math/addition")
@Slf4j
@RequiredArgsConstructor
public class AdditionController {
    private final NumberGeneratorService numberGeneratorService;
    private final CalculationService calculationService;

    @PostMapping("/verify-all-with-carries")
    public ResponseEntity<Map<String, Object>> verifyAllAnswersWithCarries(
            @RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        Map<String, Object> results = calculationService.calculateSum(userAnswerWithQuestions);
        return ResponseEntity.ok(
                results
        );
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
//        numberGeneratorService.printArray(array);
        return numberGeneratorService.generateRandom2DArray(rows, cols, min, max);
    }


}