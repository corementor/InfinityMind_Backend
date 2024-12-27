package org.test.mindexpanseweb.service;

import org.springframework.stereotype.Service;
import org.test.mindexpanseweb.dto.MathProblemDto;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProblemGeneratorService {
    private final SecureRandom random = new SecureRandom();

    public List<MathProblemDto> generateAdditionWorksheet(int problemCount, boolean withRegrouping) {
        return IntStream.range(0, problemCount)
                .mapToObj(i -> generateAdditionProblem(withRegrouping))
                .collect(Collectors.toList());
    }



    private MathProblemDto generateAdditionProblem(boolean withRegrouping) {
        int firstNumber, secondNumber;

        if (withRegrouping) {
            // Allow full range of addition problems
            firstNumber = random.nextInt(1, 100);
            secondNumber = random.nextInt(1, 100);
        } else {
            // Ensure no regrouping by limiting digit placement
            firstNumber = generateSinglePlacementNumber();
            secondNumber = generateSinglePlacementNumber();
        }

        return new MathProblemDto(
                null,
                firstNumber,
                secondNumber,
                withRegrouping ? "ADDITION_WITH_REGROUPING" : "ADDITION_WITHOUT_REGROUPING",
                firstNumber + secondNumber,
                null,
                false
        );
    }

    private int generateSinglePlacementNumber() {
        return random.nextInt(1, 50);
    }
}
