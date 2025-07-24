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

    /**
     * Verifies the user's answers against the correct answers and returns the results.
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
    @PostMapping("/verify-subtractions")
    public ResponseEntity<Map<String, Object>> verifySubtractionsAnswers(@RequestBody List<Map<String, Object>> userAnswerWithQuestions) {
        Map<String, Object> results = calculationService.calculateSubtraction(userAnswerWithQuestions);
        return ResponseEntity.ok(
                results
        );
    }

    @PostMapping("/verify-multiplication")
    public Map<String, Object> verifyMultiplication(@RequestBody List<Map<String, Object>> userAnswers) {
        List<String> results = new ArrayList<>();
        List<Map<String, Integer>> correctCarriesList = new ArrayList<>();
        List<Map<String, Boolean>> carryValidationList = new ArrayList<>();
        List<Map<String, Integer>> correctPartialProductsList = new ArrayList<>();
        int totalScore = 0;
        final int MAX_SCORE_PER_QUESTION = 4;

        for (Map<String, Object> answer : userAnswers) {
            int number1 = (int) answer.get("number1");
            int number2 = (int) answer.get("number2");
            int userAnswer = (int) answer.get("answer");

            Map<String, Integer> userCarries = (Map<String, Integer>) answer.get("carries");
            Map<String, Integer> userPartialProducts = (Map<String, Integer>) answer.get("partialProducts");

            // Calculate correct values
            int correctAnswer = number1 * number2;

            // Calculate partial products
            int partialProduct1 = number1 * (number2 % 10);
            int partialProduct2 = number1 * ((number2 / 10) % 10) * 10;
            int partialProduct3 = number1 * ((number2 / 100) % 10) * 100;

            // Calculate carries
            int carryOnesToTens = (number1 % 10 * number2 % 10) / 10;
            int carryTensToHundreds = (number1 % 100 / 10 * number2 % 10 + carryOnesToTens) / 10;

            // Verify user inputs
            boolean answerCorrect = userAnswer == correctAnswer;
            boolean partialProductsCorrect =
                    userPartialProducts.get("partialProduct1") == partialProduct1 &&
                            userPartialProducts.get("partialProduct2") == partialProduct2 &&
                            userPartialProducts.get("partialProduct3") == partialProduct3;
            boolean carriesCorrect =
                    userCarries.get("carryOnesToTens") == carryOnesToTens &&
                            userCarries.get("carryTensToHundreds") == carryTensToHundreds;

            // Calculate score
            int questionScore = 0;
            if (answerCorrect) {
                questionScore += 2;
                if (partialProductsCorrect) questionScore += 1;
                if (carriesCorrect) questionScore += 1;
            }
            totalScore += questionScore;

            // Prepare response
            String resultMessage;
            if (answerCorrect && partialProductsCorrect && carriesCorrect) {
                resultMessage = "Perfect! All steps correct";
            } else if (answerCorrect && partialProductsCorrect) {
                resultMessage = "Answer and partial products correct but carries wrong";
            } else if (answerCorrect && carriesCorrect) {
                resultMessage = "Answer and carries correct but partial products wrong";
            } else if (answerCorrect) {
                resultMessage = "Only final answer correct";
            } else if (partialProductsCorrect && carriesCorrect) {
                resultMessage = "Steps correct but final answer wrong";
            } else {
                resultMessage = "Multiple errors in calculation";
            }

            results.add(resultMessage);
            correctCarriesList.add(Map.of(
                    "carryOnesToTens", carryOnesToTens,
                    "carryTensToHundreds", carryTensToHundreds
            ));
            carryValidationList.add(Map.of(
                    "carryOnesToTensCorrect", carriesCorrect,
                    "carryTensToHundredsCorrect", carriesCorrect
            ));
            correctPartialProductsList.add(Map.of(
                    "partialProduct1", partialProduct1,
                    "partialProduct2", partialProduct2,
                    "partialProduct3", partialProduct3
            ));
        }

        int maxPossibleScore = userAnswers.size() * MAX_SCORE_PER_QUESTION;
        int percentageScore = maxPossibleScore > 0 ? (totalScore * 100) / maxPossibleScore : 0;

        return Map.of(
                "results", results,
                "score", totalScore,
                "percentage", percentageScore,
                "maxScore", maxPossibleScore,
                "total", userAnswers.size(),
                "correctCarries", correctCarriesList,
                "carryValidation", carryValidationList,
                "correctPartialProducts", correctPartialProductsList
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