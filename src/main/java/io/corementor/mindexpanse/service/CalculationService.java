package io.corementor.mindexpanse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CalculationService {
    private final NumberGeneratorService numberGeneratorService;

    /**
     *  Calculates the sum of the user's answers and returns the results, score, and total number of questions.
     * @param userAnswerWithQuestions the user's answers
     * @return Map containing the results, score, and total number of questions
     */
    public Map<String, Object> calculateSum(List<Map<String, Object>> userAnswerWithQuestions) {
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
            int[] digits1 = numberGeneratorService.extractDigits(number1);
            int[] digits2 = numberGeneratorService.extractDigits(number2);
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

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("results", results);
        finalResult.put("score", totalScore);
        finalResult.put("percentage", percentageScore);
        finalResult.put("maxScore", maxPossibleScore);
        finalResult.put("total", userAnswerWithQuestions.size());
        finalResult.put("correctCarries", correctCarriesList);
        finalResult.put("carryValidation", carryValidationResults);
        return finalResult;
    }

    /**
     *  Verifies the user's answers against the correct answers and returns the results.
     * @param userAnswerWithQuestions the user's answers
     * @return Map containing the results, score, and total number of questions
     */

    public Map<String, Object> calculateSubtraction(List<Map<String, Object>> userAnswerWithQuestions) {
        List<String> results = new ArrayList<>();
        List<Map<String, Boolean>> borrowValidationResults = new ArrayList<>();
        List<Map<String, Integer>> correctBorrowsList = new ArrayList<>();
        int totalScore = 0;
        final int MAX_SCORE_PER_QUESTION = 3;

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            // Get user's borrow inputs (matching frontend property names)
            Map<String, Integer> userBorrows = (Map<String, Integer>) questionAnswer.get("borrows");
            int userTensBorrow = userBorrows.getOrDefault("tens", 0);
            int userHundredsBorrow = userBorrows.getOrDefault("hundreds", 0);
            int userThousandsBorrow = userBorrows.getOrDefault("thousands", 0);

            // Calculate step-by-step
            int[] digits1 = numberGeneratorService.extractDigits(number1);
            int[] digits2 = numberGeneratorService.extractDigits(number2);
            int correctAnswer = number1 - number2;

            Map<String, Integer> correctBorrows = new HashMap<>();
            Map<String, Boolean> borrowValidation = new HashMap<>();
            int questionScore = 0;
            boolean[] borrowed = new boolean[4]; // ones, tens, hundreds, thousands (we won't use ones)

            // Calculate subtraction from right to left (ones to thousands)
            // 1. Ones place
            int onesDiff = digits1[0] - digits2[0];
            boolean tensBorrowed = false;
            if (onesDiff < 0) {
                tensBorrowed = true;
                onesDiff += 10;
                digits1[1]--; // Borrow from tens
            }
            boolean tensCorrect = (userTensBorrow == 1 && tensBorrowed) || (userTensBorrow == 0 && !tensBorrowed);
            borrowValidation.put("tensCorrect", tensCorrect);
            correctBorrows.put("tens", tensBorrowed ? 1 : 0);

            // 2. Tens place
            int tensDiff = digits1[1] - digits2[1];
            boolean hundredsBorrowed = false;
            if (tensDiff < 0) {
                hundredsBorrowed = true;
                tensDiff += 10;
                digits1[2]--; // Borrow from hundreds
            }
            boolean hundredsCorrect = (userHundredsBorrow == 1 && hundredsBorrowed) || (userHundredsBorrow == 0 && !hundredsBorrowed);
            borrowValidation.put("hundredsCorrect", hundredsCorrect);
            correctBorrows.put("hundreds", hundredsBorrowed ? 1 : 0);

            // 3. Hundreds place
            int hundredsDiff = digits1[2] - digits2[2];
            boolean thousandsBorrowed = false;
            if (hundredsDiff < 0) {
                thousandsBorrowed = true;
                hundredsDiff += 10;
                digits1[3]--; // Borrow from thousands
            }
            boolean thousandsCorrect = (userThousandsBorrow == 1 && thousandsBorrowed) || (userThousandsBorrow == 0 && !thousandsBorrowed);
            borrowValidation.put("thousandsCorrect", thousandsCorrect);
            correctBorrows.put("thousands", thousandsBorrowed ? 1 : 0);

            boolean allBorrowsCorrect = tensCorrect && hundredsCorrect && thousandsCorrect;
            boolean answerCorrect = correctAnswer == userAnswer;

            // Scoring (same as addition)
            if (answerCorrect) {
                questionScore += 2;
                if (allBorrowsCorrect) {
                    questionScore += 1;
                }
            }
            totalScore += questionScore;

            // Result message
            String resultMessage;
            if (answerCorrect && allBorrowsCorrect) {
                resultMessage = "Perfect! Answer and borrows all correct";
            } else if (answerCorrect) {
                resultMessage = "Answer correct but some borrows wrong";
            } else if (allBorrowsCorrect) {
                resultMessage = "Borrows correct but answer wrong";
            } else {
                resultMessage = "Both answer and borrows incorrect";
            }

            results.add(resultMessage);
            borrowValidationResults.add(borrowValidation);
            correctBorrowsList.add(correctBorrows);
        }

        int maxPossibleScore = userAnswerWithQuestions.size() * MAX_SCORE_PER_QUESTION;
        int percentageScore = maxPossibleScore > 0 ? (totalScore * 100) / maxPossibleScore : 0;

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("results", results);
        finalResult.put("score", totalScore);
        finalResult.put("percentage", percentageScore);
        finalResult.put("maxScore", maxPossibleScore);
        finalResult.put("total", userAnswerWithQuestions.size());
        finalResult.put("correctBorrows", correctBorrowsList);
        finalResult.put("borrowValidation", borrowValidationResults);
        return finalResult;
    }



    /**
     * Verifies the user's multiplication answers and returns the results
     * @param userAnswerWithQuestions the user's answers
     * @return Map containing the results, score, and validation details
     */
    public Map<String, Object> calculateMultiplication(List<Map<String, Object>> userAnswerWithQuestions) {
        List<String> results = new ArrayList<>();
        List<Map<String, Boolean>> carryValidationResults = new ArrayList<>();
        List<Map<String, Integer>> correctCarriesList = new ArrayList<>();
        List<Map<String, Integer>> partialProductsList = new ArrayList<>();
        int totalScore = 0;
        final int MAX_SCORE_PER_QUESTION = 4; // Higher max score due to complexity

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            // Get user's carry inputs and partial products
            Map<String, Integer> userCarries = (Map<String, Integer>) questionAnswer.get("carries");
            Map<String, Integer> userPartialProducts = (Map<String, Integer>) questionAnswer.get("partialProducts");

            int[] digits1 = numberGeneratorService.extractDigits(number1);
            int[] digits2 = numberGeneratorService.extractDigits(number2);
            int correctAnswer = number1 * number2;

            // Calculate correct multiplication steps
            Map<String, Integer> correctCarries = new HashMap<>();
            Map<String, Boolean> carryValidation = new HashMap<>();
            Map<String, Integer> correctPartialProducts = new HashMap<>();
            int questionScore = 0;

            // Calculate partial products and carries
            int partialProduct1 = number1 * digits2[0]; // Multiply by ones digit
            int partialProduct2 = number1 * digits2[1] * 10; // Multiply by tens digit
            int partialProduct3 = number1 * digits2[2] * 100; // Multiply by hundreds digit

            // Store correct partial products
            correctPartialProducts.put("partialProduct1", partialProduct1);
            correctPartialProducts.put("partialProduct2", partialProduct2);
            correctPartialProducts.put("partialProduct3", partialProduct3);

            // Verify user's partial products
            boolean partialProductsCorrect =
                    userPartialProducts.getOrDefault("partialProduct1", -1) == partialProduct1 &&
                            userPartialProducts.getOrDefault("partialProduct2", -1) == partialProduct2 &&
                            userPartialProducts.getOrDefault("partialProduct3", -1) == partialProduct3;

            // Calculate carries for each multiplication step
            int carryOnesToTens = (digits1[0] * digits2[0]) / 10;
            int carryTensToHundreds = (digits1[1] * digits2[0] + (digits1[0] * digits2[0]) / 10) / 10;

            correctCarries.put("carryOnesToTens", carryOnesToTens);
            correctCarries.put("carryTensToHundreds", carryTensToHundreds);

            // Verify user's carries
            boolean carriesCorrect =
                    userCarries.getOrDefault("carryOnesToTens", -1) == carryOnesToTens &&
                            userCarries.getOrDefault("carryTensToHundreds", -1) == carryTensToHundreds;

            boolean answerCorrect = correctAnswer == userAnswer;

            // Scoring - more points available due to complexity
            if (answerCorrect) {
                questionScore += 2;
                if (carriesCorrect) {
                    questionScore += 1;
                }
                if (partialProductsCorrect) {
                    questionScore += 1;
                }
            }
            totalScore += questionScore;

            // Result message
            String resultMessage;
            if (answerCorrect && carriesCorrect && partialProductsCorrect) {
                resultMessage = "Perfect! All steps correct";
            } else if (answerCorrect && carriesCorrect) {
                resultMessage = "Answer and carries correct but partial products wrong";
            } else if (answerCorrect && partialProductsCorrect) {
                resultMessage = "Answer and partial products correct but carries wrong";
            } else if (answerCorrect) {
                resultMessage = "Only final answer correct";
            } else if (carriesCorrect && partialProductsCorrect) {
                resultMessage = "Steps correct but final answer wrong";
            } else {
                resultMessage = "Multiple errors in calculation";
            }

            results.add(resultMessage);
            carryValidationResults.add(carryValidation);
            correctCarriesList.add(correctCarries);
            partialProductsList.add(correctPartialProducts);
        }

        int maxPossibleScore = userAnswerWithQuestions.size() * MAX_SCORE_PER_QUESTION;
        int percentageScore = maxPossibleScore > 0 ? (totalScore * 100) / maxPossibleScore : 0;

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("results", results);
        finalResult.put("score", totalScore);
        finalResult.put("percentage", percentageScore);
        finalResult.put("maxScore", maxPossibleScore);
        finalResult.put("total", userAnswerWithQuestions.size());
        finalResult.put("correctCarries", correctCarriesList);
        finalResult.put("correctPartialProducts", partialProductsList);
        finalResult.put("carryValidation", carryValidationResults);
        return finalResult;
    }

}
