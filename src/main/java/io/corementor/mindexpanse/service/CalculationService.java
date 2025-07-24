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

}
