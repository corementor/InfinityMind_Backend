package io.corementor.infinitymind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivisionRequest {
    private int numberOfQuestions;
    private String complexity;
    private String numberOfDigits;

    // Validation
    public boolean isValid() {
        return numberOfQuestions > 0 && numberOfQuestions <= 50 &&
                (complexity.equals("with-remainder") || complexity.equals("without-remainder")) &&
                (numberOfDigits.equals("1-digit") || numberOfDigits.equals("2-digit") || numberOfDigits.equals("mixed"));
    }
}