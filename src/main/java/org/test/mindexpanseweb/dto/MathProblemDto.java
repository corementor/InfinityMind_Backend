package org.test.mindexpanseweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MathProblemDto {
    private Long id;
    private int firstNumber;
    private int secondNumber;
    private String operationType;
    private int correctAnswer;
    private Integer userAnswer;
    private boolean isCorrect;

    public MathProblemDto() {
    }

    public MathProblemDto(Long id, int firstNumber, int secondNumber, String operationType, int correctAnswer, Integer userAnswer, boolean isCorrect) {
        this.id = id;
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.operationType = operationType;
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(int firstNumber) {
        this.firstNumber = firstNumber;
    }

    public int getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(int secondNumber) {
        this.secondNumber = secondNumber;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Integer getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(Integer userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }


}
