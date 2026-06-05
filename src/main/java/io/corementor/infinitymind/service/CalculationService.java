package io.corementor.infinitymind.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The class Calculation Service.
 *
 * @author Blaise Mugisha
 * @version 1.0
 */

@RequiredArgsConstructor
@Service
public class CalculationService {
    /** The Number Generator service */
    private final NumberGeneratorService numberGeneratorService;

    /**
     *  Calculates the sum of the user's answers and returns the results, score, and total number of questions.
     * @param userAnswerWithQuestions the user's answers
     * @return Map containing the results, score,percentage,max score,totalQuestions, correctCarries, and carry validations.
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
     * @return Map containing the results, score, percentage,maxScore,total questions,correct borrows and borrow validations.
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

            // Get user's borrow inputs
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
     * Calculate Multiplication
     * Verifies the user's multiplication answers and returns the results
     * @param userAnswerWithQuestions the user's answers
     * @return Map containing the results, score,percentage,maxScore,total questions,correct carries,correct partial products and carry validations
     */

    public Map<String, Object> calculateMultiplication(List<Map<String, Object>> userAnswerWithQuestions) {
        List<String> results = new ArrayList<>();
        List<Map<String, Boolean>> carryValidationResults = new ArrayList<>();
        List<Map<String, Integer>> correctCarriesList = new ArrayList<>();
        List<Map<String, Integer>> partialProductsList = new ArrayList<>();
        int totalScore = 0;
        final int MAX_SCORE_PER_QUESTION = 4;

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int number1 = ((Number) questionAnswer.get("number1")).intValue();
            int number2 = ((Number) questionAnswer.get("number2")).intValue();
            int userAnswer = ((Number) questionAnswer.get("answer")).intValue();

            Map<String, Integer> userCarries = (Map<String, Integer>) questionAnswer.get("carries");
            Map<String, Integer> userPartialProducts = (Map<String, Integer>) questionAnswer.get("partialProducts");

            int correctAnswer = number1 * number2;

            // Extract digits
            int[] digits2 = extractDigitsDynamic(number2);

            // Calculate CORRECT partial products (with place values)
            Map<String, Integer> correctPartialProducts = new HashMap<>();

            // PP1: number1 × ones digit
            int pp1 = number1 * (number2 % 10);
            correctPartialProducts.put("partialProduct1", pp1);

            // PP2: number1 × tens digit × 10
            int pp2 = number1 * ((number2 / 10) % 10) * 10;
            correctPartialProducts.put("partialProduct2", pp2);

            // PP3 and PP4 are 0 for 2-digit numbers
            correctPartialProducts.put("partialProduct3", 0);
            correctPartialProducts.put("partialProduct4", 0);

            // Calculate CORRECT carries
            Map<String, Integer> correctCarries = new HashMap<>();
            int[] digits1 = extractDigitsDynamic(number1);

            // Carries for ones-digit multiplication
            int carry1 = (digits1[0] * digits2[0]) / 10; // ones × ones carry
            int intermediate = (digits1[1] * digits2[0]) + carry1;
            int carry2 = intermediate / 10; // tens × ones + carry

            correctCarries.put("carryOnesToTens", carry1);
            correctCarries.put("carryTensToHundreds", carry2);
            correctCarries.put("carryHundredsToThousands", 0);
            correctCarries.put("carryThousandsToTenThousands", 0);

            // Check user inputs against correct values
            boolean pp1Correct = userPartialProducts.getOrDefault("partialProduct1", -1) == pp1;
            boolean pp2Correct = userPartialProducts.getOrDefault("partialProduct2", -1) == pp2;
            boolean pp3Correct = userPartialProducts.getOrDefault("partialProduct3", 0) == 0;
            boolean pp4Correct = userPartialProducts.getOrDefault("partialProduct4", 0) == 0;

            boolean carry1Correct = userCarries.getOrDefault("carryOnesToTens", -1) == carry1;
            boolean carry2Correct = userCarries.getOrDefault("carryTensToHundreds", -1) == carry2;
            boolean carry3Correct = userCarries.getOrDefault("carryHundredsToThousands", 0) == 0;
            boolean carry4Correct = userCarries.getOrDefault("carryThousandsToTenThousands", 0) == 0;

            boolean answerCorrect = (correctAnswer == userAnswer);

            // SIMPLE SCORING - Only check relevant components for 2-digit numbers
            int questionScore = 0;

            if (answerCorrect) {
                questionScore += 2; // Base points for correct answer

                // For 2-digit numbers, only check PP1 and PP2
                if (pp1Correct && pp2Correct) {
                    questionScore += 1;
                }

                // For 2-digit numbers, only check the first 2 carries
                if (carry1Correct && carry2Correct) {
                    questionScore += 1;
                }
            }

            totalScore += questionScore;

            // Result message based on actual performance
            String resultMessage;
            if (answerCorrect && pp1Correct && pp2Correct && carry1Correct && carry2Correct) {
                resultMessage = "Perfect! All correct";
            } else if (answerCorrect && pp1Correct && pp2Correct) {
                resultMessage = "Answer and partial products correct";
            } else if (answerCorrect && carry1Correct && carry2Correct) {
                resultMessage = "Answer and carries correct";
            } else if (answerCorrect) {
                resultMessage = "Only final answer correct";
            } else {
                resultMessage = "Answer incorrect";
            }

            results.add(resultMessage);

            // Validation for UI - show what was actually correct/wrong
            Map<String, Boolean> validation = new HashMap<>();
            validation.put("partialProduct1Correct", pp1Correct);
            validation.put("partialProduct2Correct", pp2Correct);
            validation.put("partialProduct3Correct", pp3Correct);
            validation.put("partialProduct4Correct", pp4Correct);

            validation.put("carryOnesToTensCorrect", carry1Correct);
            validation.put("carryTensToHundredsCorrect", carry2Correct);
            validation.put("carryHundredsToThousandsCorrect", carry3Correct);
            validation.put("carryThousandsToTenThousandsCorrect", carry4Correct);

            validation.put("finalAnswerCorrect", answerCorrect);

            carryValidationResults.add(validation);
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
    /**
     * Extracts digits from a number dynamically based on its length.
     * @param number int
     * @return int[]
     */
    private int[] extractDigitsDynamic(int number) {
        if (number == 0) return new int[]{0};

        int temp = number;
        int digitCount = 0;
        while (temp > 0) {
            digitCount++;
            temp /= 10;
        }

        int[] digits = new int[digitCount];
        temp = number;
        for (int i = 0; i < digitCount; i++) {
            digits[i] = temp % 10;
            temp /= 10;
        }

        return digits;
    }






    // Helper method to get carry key names
    private String getCarryKey(int index) {
        switch (index) {
            case 0: return "carryOnesToTens";
            case 1: return "carryTensToHundreds";
            case 2: return "carryHundredsToThousands";
            case 3: return "carryThousandsToTenThousands";
            default: return "carryOnesToTens";
        }
    }

    public Map<String, Object> calculateDivision(List<Map<String, Object>> userAnswerWithQuestions) {
        List<String> results = new ArrayList<>();
        List<Map<String, Boolean>> stepValidationResults = new ArrayList<>();
        List<Map<String, Integer>> correctStepsList = new ArrayList<>();
        List<Map<String, Integer>> correctRemaindersList = new ArrayList<>();
        int totalScore = 0;
        final int MAX_SCORE_PER_QUESTION = 6; // More granular scoring

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int dividend = ((Number) questionAnswer.get("number1")).intValue();
            int divisor = ((Number) questionAnswer.get("number2")).intValue();
            int userQuotient = ((Number) questionAnswer.get("answer")).intValue();
            int userRemainder = ((Number) questionAnswer.get("remainder")).intValue();

            // Get user's step inputs
            Map<String, Integer> userSteps = (Map<String, Integer>) questionAnswer.get("steps");
            Map<String, Integer> userIntermediateRemainders = (Map<String, Integer>) questionAnswer.get("intermediateRemainders");

            // Calculate correct answer
            int correctQuotient = dividend / divisor;
            int correctFinalRemainder = dividend % divisor;

            // Perform step-by-step long division
            DivisionStepResult stepResult = performLongDivision(dividend, divisor);

            Map<String, Integer> correctSteps = stepResult.getSteps();
            Map<String, Integer> correctIntermediateRemainders = stepResult.getIntermediateRemainders();
            Map<String, Boolean> stepValidation = new HashMap<>();

            int questionScore = 0;

            // Validate each step
            boolean step1Correct = validateStep(userSteps, correctSteps, "step1");
            boolean step2Correct = validateStep(userSteps, correctSteps, "step2");
            boolean step3Correct = validateStep(userSteps, correctSteps, "step3");

            boolean remainder1Correct = validateStep(userIntermediateRemainders, correctIntermediateRemainders, "remainder1");
            boolean remainder2Correct = validateStep(userIntermediateRemainders, correctIntermediateRemainders, "remainder2");
            boolean remainder3Correct = validateStep(userIntermediateRemainders, correctIntermediateRemainders, "remainder3");

            // Store validation results
            stepValidation.put("step1Correct", step1Correct);
            stepValidation.put("step2Correct", step2Correct);
            stepValidation.put("step3Correct", step3Correct);
            stepValidation.put("remainder1Correct", remainder1Correct);
            stepValidation.put("remainder2Correct", remainder2Correct);
            stepValidation.put("remainder3Correct", remainder3Correct);

            boolean quotientCorrect = correctQuotient == userQuotient;
            boolean finalRemainderCorrect = correctFinalRemainder == userRemainder;

            // Scoring system (6 points total)
            if (quotientCorrect) {
                questionScore += 3; // 3 points for correct final quotient
            }

            if (finalRemainderCorrect) {
                questionScore += 1; // 1 point for correct final remainder
            }

            // Award points for correct working steps
            int correctStepsCount = 0;
            if (step1Correct && remainder1Correct) correctStepsCount++;
            if (step2Correct && remainder2Correct) correctStepsCount++;
            if (step3Correct && remainder3Correct) correctStepsCount++;

            questionScore += Math.min(2, correctStepsCount); // Max 2 points for working

            totalScore += questionScore;

            // Generate detailed result message
            String resultMessage = generateDetailedResultMessage(
                    quotientCorrect, finalRemainderCorrect, correctStepsCount, 3);

            results.add(resultMessage);
            stepValidationResults.add(stepValidation);
            correctStepsList.add(correctSteps);
            correctRemaindersList.add(correctIntermediateRemainders);
        }

        int maxPossibleScore = userAnswerWithQuestions.size() * MAX_SCORE_PER_QUESTION;
        int percentageScore = maxPossibleScore > 0 ? (totalScore * 100) / maxPossibleScore : 0;

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("results", results);
        finalResult.put("score", totalScore);
        finalResult.put("percentage", percentageScore);
        finalResult.put("maxScore", maxPossibleScore);
        finalResult.put("total", userAnswerWithQuestions.size());
        finalResult.put("correctSteps", correctStepsList);
        finalResult.put("correctRemainders", correctRemaindersList);
        finalResult.put("stepValidation", stepValidationResults);

        return finalResult;
    }

    /**
     * Performs step-by-step long division matching real-life method
     */
    private DivisionStepResult performLongDivision(int dividend, int divisor) {
        String dividendStr = String.valueOf(dividend);
        int dividendLength = dividendStr.length();

        Map<String, Integer> steps = new HashMap<>();
        Map<String, Integer> intermediateRemainders = new HashMap<>();

        int currentRemainder = 0;
        int stepCount = 0;

        // Process each digit of dividend from left to right
        for (int i = 0; i < dividendLength && stepCount < 3; i++) {
            int currentDigit = Character.getNumericValue(dividendStr.charAt(i));
            int currentDividend = currentRemainder * 10 + currentDigit;

            // Skip if current dividend is smaller than divisor (except for last digits)
            if (currentDividend < divisor && i < dividendLength - 1) {
                currentRemainder = currentDividend;
                continue;
            }

            stepCount++;
            int stepQuotient = currentDividend / divisor;
            int stepRemainder = currentDividend % divisor;

            steps.put("step" + stepCount, stepQuotient);
            intermediateRemainders.put("remainder" + stepCount, stepRemainder);

            currentRemainder = stepRemainder;
        }

        // Fill remaining steps with 0 if not used
        for (int i = stepCount + 1; i <= 3; i++) {
            steps.put("step" + i, 0);
            intermediateRemainders.put("remainder" + i, 0);
        }

        return new DivisionStepResult(steps, intermediateRemainders);
    }

    /**
     * Helper method to validate individual steps
     *
     */
    private boolean validateStep(Map<String, Integer> userValues, Map<String, Integer> correctValues, String key) {
        int userValue = userValues.getOrDefault(key, -999);
        int correctValue = correctValues.getOrDefault(key, 0);
        return userValue == correctValue;
    }

    /**
     * Generate detailed result message based on performance
     */
    private String generateDetailedResultMessage(boolean quotientCorrect, boolean remainderCorrect,
                                                 int correctSteps, int totalSteps) {
        if (quotientCorrect && remainderCorrect && correctSteps == totalSteps) {
            return "Perfect! All steps, quotient, and remainder correct";
        } else if (quotientCorrect && remainderCorrect && correctSteps > 0) {
            return "Quotient and remainder correct, most working steps correct";
        } else if (quotientCorrect && remainderCorrect) {
            return "Final answer correct but working steps need improvement";
        } else if (quotientCorrect && correctSteps > totalSteps / 2) {
            return "Quotient correct with good working, check remainder";
        } else if (quotientCorrect) {
            return "Quotient correct but remainder and working steps incorrect";
        } else if (correctSteps > totalSteps / 2) {
            return "Working steps mostly correct but final calculation wrong";
        } else {
            return "Multiple errors - review long division method";
        }
    }

    /**
     * Helper class to store division step results
     */
    private static class DivisionStepResult {
        private final Map<String, Integer> steps;
        private final Map<String, Integer> intermediateRemainders;

        public DivisionStepResult(Map<String, Integer> steps, Map<String, Integer> intermediateRemainders) {
            this.steps = steps;
            this.intermediateRemainders = intermediateRemainders;
        }

        public Map<String, Integer> getSteps() { return steps; }
        public Map<String, Integer> getIntermediateRemainders() { return intermediateRemainders; }
    }

}
