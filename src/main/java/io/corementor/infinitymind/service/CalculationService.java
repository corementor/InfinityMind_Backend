package io.corementor.infinitymind.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The class CalculationService.
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
    /*public Map<String, Object> calculateMultiplication(List<Map<String, Object>> userAnswerWithQuestions) {
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
    }*/

/*
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

            // Extract digits dynamically based on number size
            int[] digits1 = extractDigitsDynamic(number1);
            int[] digits2 = extractDigitsDynamic(number2);

            // Determine how many partial products we need (based on number2's digits)
            int maxDigits = Math.max(digits1.length, digits2.length);
            int partialProductsCount = digits2.length; // One partial product per digit in number2

            // Calculate partial products with correct place values
            Map<String, Integer> correctPartialProducts = new HashMap<>();
            List<Integer> calculatedPartialProducts = new ArrayList<>();

            for (int i = 0; i < partialProductsCount; i++) {
                int multiplier = digits2[i]; // Get digit at position i
                int partialProduct = number1 * multiplier;
                correctPartialProducts.put("partialProduct" + (i + 1), partialProduct);
                calculatedPartialProducts.add(partialProduct);
            }

            // Fill remaining partial products with 0 if needed
            for (int i = partialProductsCount; i < 4; i++) {
                correctPartialProducts.put("partialProduct" + (i + 1), 0);
            }

            // Calculate carries for EACH partial product multiplication
            Map<String, Integer> correctCarries = new HashMap<>();

            // Calculate carries for each digit position in the multiplication
            int carry = 0;
            int carryIndex = 0;

            // Calculate carries for the ones-digit multiplication (first partial product)
            for (int i = 0; i < digits1.length; i++) {
                int product = digits1[i] * digits2[0] + carry;
                int digit = product % 10;
                carry = product / 10;

                if (i == 0) {
                    correctCarries.put("carryOnesToTens", carry);
                    carryIndex++;
                } else if (i == 1) {
                    correctCarries.put("carryTensToHundreds", carry);
                    carryIndex++;
                } else if (i == 2) {
                    correctCarries.put("carryHundredsToThousands", carry);
                    carryIndex++;
                } else if (i == 3) {
                    correctCarries.put("carryThousandsToTenThousands", carry);
                    carryIndex++;
                }
            }

            // Fill remaining carries with 0
            while (carryIndex < 4) {
                correctCarries.put(getCarryKey(carryIndex), 0);
                carryIndex++;
            }

            // Verify user inputs
            boolean allPartialProductsCorrect = true;
            for (int i = 0; i < 4; i++) {
                String key = "partialProduct" + (i + 1);
                int userPP = userPartialProducts.getOrDefault(key, -1);
                int correctPP = correctPartialProducts.getOrDefault(key, -2);
                if (userPP != correctPP) {
                    allPartialProductsCorrect = false;
                    break;
                }
            }

            boolean allCarriesCorrect = true;
            for (int i = 0; i < 4; i++) {
                String key = getCarryKey(i);
                int userCarry = userCarries.getOrDefault(key, -1);
                int correctCarry = correctCarries.getOrDefault(key, -2);
                if (userCarry != correctCarry) {
                    allCarriesCorrect = false;
                    break;
                }
            }

            boolean answerCorrect = (correctAnswer == userAnswer);

            // Scoring
            int questionScore = 0;

            if (answerCorrect) {
                questionScore += 2; // Base points for correct answer

                if (allPartialProductsCorrect) {
                    questionScore += 1;
                }

                if (allCarriesCorrect) {
                    questionScore += 1;
                }
            }

            totalScore += questionScore;

            // Result message
            String resultMessage;
            if (answerCorrect && allPartialProductsCorrect && allCarriesCorrect) {
                resultMessage = "Perfect! All correct";
            } else if (answerCorrect && allPartialProductsCorrect) {
                resultMessage = "Answer and partial products correct";
            } else if (answerCorrect && allCarriesCorrect) {
                resultMessage = "Answer and carries correct";
            } else if (answerCorrect) {
                resultMessage = "Only final answer correct";
            } else if (allPartialProductsCorrect && allCarriesCorrect) {
                resultMessage = "Steps correct but final answer wrong";
            } else {
                resultMessage = "Multiple errors in calculation";
            }

            results.add(resultMessage);

            // Store validation for UI
            Map<String, Boolean> validation = new HashMap<>();
            for (int i = 0; i < 4; i++) {
                String ppKey = "partialProduct" + (i + 1);
                int userPP = userPartialProducts.getOrDefault(ppKey, -1);
                int correctPP = correctPartialProducts.getOrDefault(ppKey, -2);
                validation.put(ppKey + "Correct", userPP == correctPP);
            }

            for (int i = 0; i < 4; i++) {
                String carryKey = getCarryKey(i);
                int userCarry = userCarries.getOrDefault(carryKey, -1);
                int correctCarry = correctCarries.getOrDefault(carryKey, -2);
                validation.put(carryKey + "Correct", userCarry == correctCarry);
            }

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
        final int MAX_SCORE_PER_QUESTION = 4; // Points for answer, quotient digits, and remainder

        for (Map<String, Object> questionAnswer : userAnswerWithQuestions) {
            int dividend = ((Number) questionAnswer.get("number1")).intValue();
            int divisor = ((Number) questionAnswer.get("number2")).intValue();
            int userQuotient = ((Number) questionAnswer.get("answer")).intValue();
            int userRemainder = ((Number) questionAnswer.get("remainder")).intValue();

            // Get user's step inputs
            Map<String, Integer> userSteps = (Map<String, Integer>) questionAnswer.get("steps");
            Map<String, Integer> userRemainders = (Map<String, Integer>) questionAnswer.get("intermediateRemainders");

            int correctQuotient = dividend / divisor;
            int correctRemainder = dividend % divisor;

            Map<String, Integer> correctSteps = new HashMap<>();
            Map<String, Boolean> stepValidation = new HashMap<>();
            Map<String, Integer> correctIntermediateRemainders = new HashMap<>();
            int questionScore = 0;

            // Calculate division steps
            int[] dividendDigits = numberGeneratorService.extractDigits(dividend);
            int[] divisorDigits = numberGeneratorService.extractDigits(divisor);

            // Long division steps
            int currentDividend = 0;
            int step = 0;
            int[] partialDividends = new int[3];
            int[] partialQuotients = new int[3];
            int[] partialRemainders = new int[3];

            // Step 1: First partial division (thousands place)
            if (dividend >= 1000) {
                currentDividend = dividendDigits[3] * 1000 + dividendDigits[2] * 100;
                partialQuotients[0] = currentDividend / divisor;
                partialRemainders[0] = currentDividend % divisor;
                partialDividends[0] = currentDividend;
                correctSteps.put("step1", partialQuotients[0]);
                correctIntermediateRemainders.put("remainder1", partialRemainders[0]);
                step++;
            }

            // Step 2: Second partial division (hundreds place)
            if (dividend >= 100) {
                currentDividend = partialRemainders[0] * 100 + dividendDigits[1] * 10;
                if (step == 0) {
                    currentDividend = dividendDigits[3] * 1000 + dividendDigits[2] * 100 + dividendDigits[1] * 10;
                }
                partialQuotients[1] = currentDividend / divisor;
                partialRemainders[1] = currentDividend % divisor;
                partialDividends[1] = currentDividend;
                correctSteps.put("step2", partialQuotients[1]);
                correctIntermediateRemainders.put("remainder2", partialRemainders[1]);
                step++;
            }

            // Step 3: Final partial division (tens and ones place)
            currentDividend = partialRemainders[1] * 10 + dividendDigits[0];
            if (step == 0) {
                currentDividend = dividend;
            } else if (step == 1) {
                currentDividend = partialRemainders[0] * 100 + dividendDigits[1] * 10 + dividendDigits[0];
            }
            partialQuotients[2] = currentDividend / divisor;
            partialRemainders[2] = currentDividend % divisor;
            partialDividends[2] = currentDividend;
            correctSteps.put("step3", partialQuotients[2]);
            correctIntermediateRemainders.put("remainder3", partialRemainders[2]);

            // Verify user's steps
            boolean stepsCorrect = true;
            for (int i = 0; i < 3; i++) {
                if (userSteps.getOrDefault("step" + (i + 1), -1) != partialQuotients[i]) {
                    stepValidation.put("step" + (i + 1) + "Correct", false);
                    stepsCorrect = false;
                } else {
                    stepValidation.put("step" + (i + 1) + "Correct", true);
                }

                if (userRemainders.getOrDefault("remainder" + (i + 1), -1) != partialRemainders[i]) {
                    stepValidation.put("remainder" + (i + 1) + "Correct", false);
                    stepsCorrect = false;
                } else {
                    stepValidation.put("remainder" + (i + 1) + "Correct", true);
                }
            }

            boolean quotientCorrect = correctQuotient == userQuotient;
            boolean remainderCorrect = correctRemainder == userRemainder;

            // Scoring
            if (quotientCorrect) {
                questionScore += 2; // Base points for correct answer
                if (remainderCorrect) {
                    questionScore += 1; // Additional point for correct remainder
                }
                if (stepsCorrect) {
                    questionScore += 1; // Additional point for correct steps
                }
            }
            totalScore += questionScore;

            // Result message
            String resultMessage;
            if (quotientCorrect && remainderCorrect && stepsCorrect) {
                resultMessage = "Perfect! All steps and answers correct";
            } else if (quotientCorrect && remainderCorrect) {
                resultMessage = "Answer correct but some steps wrong";
            } else if (quotientCorrect && stepsCorrect) {
                resultMessage = "Quotient and steps correct but remainder wrong";
            } else if (quotientCorrect) {
                resultMessage = "Only final quotient correct";
            } else if (stepsCorrect) {
                resultMessage = "Steps correct but final answer wrong";
            } else {
                resultMessage = "Multiple errors in calculation";
            }

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

}
