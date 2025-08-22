package io.corementor.mindexpanse.dto;

import lombok.Getter;

@Getter
public class DivisionQuestion {
    private final int number1; // dividend
    private  final int number2; // divisor

    public DivisionQuestion(int number1, int number2) {
        this.number1 = number1;
        this.number2 = number2;
    }

}
