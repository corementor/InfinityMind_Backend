package io.corementor.mindexpanse.controller;

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
@RequestMapping("/api/math/addition")
@Slf4j
@RequiredArgsConstructor
public class AdditionController {
    private static final Logger logger = LoggerFactory.getLogger(AdditionController.class);
    private final Random random = new Random();
    private final NumberGeneratorService numberGeneratorService;

    /**
     * Generates a random number within a specified range.
     *
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @return A random integer between min and max.
     */
    private int generateRandomNumber(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
    /*private int generateRandomNumber(int min, int max) {
        // Calculate the range size
        int range = max - min + 1;

        // Create a 2x3 array to generate multiple random numbers
        int array[][] = new int[2][3];
        Random random = new Random();

        // Fill the array with random numbers in the specified range
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = min + random.nextInt(range);
            }
        }

        // Select and return one random element from the array
        int randomRow = random.nextInt(array.length);
        int randomCol = random.nextInt(array[0].length);
        return array[randomRow][randomCol];
    }*/

    /**
     * Generates two numbers that, when added together, won't exceed 100.
     *
     * @param singleDigit If true, generates single-digit numbers; otherwise, generates two-digit numbers.
     * @return A map containing the two numbers ("number1" and "number2").
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

    /**
     * Generates two numbers based on the specified type (singleDigit or fourDigit).
     *
     * @param type The type of numbers to generate ("singleDigit" or "fourDigit").
     * @return A map containing the two numbers ("number1" and "number2").
     */
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

    /**
     * Verifies the user's answers against the correct answers and returns the results.
     *
     * @param userAnswerWithQuestions A list of maps containing the user's answers and the corresponding questions.
     * @return A ResponseEntity containing the results, score, and total number of questions.
     */


    @PostMapping("/verify-all-with-carries")
    public ResponseEntity<Map<String, Object>> verifyAllAnswersWithCarries(
            @RequestBody List<Map<String, Object>> userAnswerWithQuestions) {

        List<String> results = new ArrayList<>();
        List<Map<String, Boolean>> carryValidationResults = new ArrayList<>();
        List<Map<String, Integer>> correctCarriesList = new ArrayList<>();
        int totalScore = 0;
        final int MAX_SCORE_PER_QUESTION = 3;

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            // Get user's carry inputs (matching frontend property names)
            Map<String, Integer> userCarries = (Map<String, Integer>) questionAnswer.get("carries");
            int userTensCarry = userCarries.getOrDefault("tens", 0);
            int userHundredsCarry = userCarries.getOrDefault("hundreds", 0);
            int userThousandsCarry = userCarries.getOrDefault("thousands", 0);

            // Calculate step-by-step
            int[] digits1 = extractDigits(number1);
            int[] digits2 = extractDigits(number2);
            int correctAnswer = number1 + number2;

            Map<String, Integer> correctCarries = new HashMap<>();
            Map<String, Boolean> carryValidation = new HashMap<>();
            int questionScore = 0;
            int carry = 0;

            // 1. Ones place (produces tens carry)
            int onesSum = digits1[0] + digits2[0];
            int computedTensCarry = onesSum / 10;
            boolean tensCorrect = userTensCarry == computedTensCarry;
            carryValidation.put("tensCorrect", tensCorrect);
            correctCarries.put("tens", computedTensCarry);
            carry = computedTensCarry;

            // 2. Tens place (carry from ones, produces hundreds carry)
            int tensSum = digits1[1] + digits2[1] + carry;
            int computedHundredsCarry = tensSum / 10;
            boolean hundredsCorrect = userHundredsCarry == computedHundredsCarry;
            carryValidation.put("hundredsCorrect", hundredsCorrect);
            correctCarries.put("hundreds", computedHundredsCarry);
            carry = computedHundredsCarry;

            // 3. Hundreds place (carry from tens, produces thousands carry)
            int hundredsSum = digits1[2] + digits2[2] + carry;
            int computedThousandsCarry = hundredsSum / 10;
            boolean thousandsCorrect = userThousandsCarry == computedThousandsCarry;
            carryValidation.put("thousandsCorrect", thousandsCorrect);
            correctCarries.put("thousands", computedThousandsCarry);

            boolean allCarriesCorrect = tensCorrect && hundredsCorrect && thousandsCorrect;
            boolean answerCorrect = correctAnswer == userAnswer;

            // Scoring
            if (answerCorrect) {
                questionScore += 2;
                if (allCarriesCorrect) {
                    questionScore += 1;
                }
            }
            totalScore += questionScore;

            // Result message
            String resultMessage;
            if (answerCorrect && allCarriesCorrect) {
                resultMessage = "Perfect! Answer and carries all correct";
            } else if (answerCorrect) {
                resultMessage = "Answer correct but some carries wrong";
            } else if (allCarriesCorrect) {
                resultMessage = "Carries correct but answer wrong";
            } else {
                resultMessage = "Both answer and carries incorrect";
            }

            results.add(resultMessage);
            carryValidationResults.add(carryValidation);
            correctCarriesList.add(correctCarries);
        }

        int maxPossibleScore = userAnswerWithQuestions.size() * MAX_SCORE_PER_QUESTION;
        int percentageScore = maxPossibleScore > 0 ? (totalScore * 100) / maxPossibleScore : 0;

        return ResponseEntity.ok(
                Map.of(
                        "results", results,
                        "score", totalScore,
                        "percentage", percentageScore,
                        "maxScore", maxPossibleScore,
                        "total", userAnswerWithQuestions.size(),
                        "correctCarries", correctCarriesList,
                        "carryValidation", carryValidationResults
                )
        );
    }

    /**
     *  extractDigits
     * @param number number
     * @return int[]
     */
    private int[] extractDigits(int number) {
        int[] digits = new int[4]; // ones, tens, hundreds, thousands
        for (int i = 0; i < 4; i++) {
            digits[i] = number % 10;
            number /= 10;
        }
        return digits;
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