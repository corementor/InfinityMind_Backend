package io.corementor.infinitymind.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The class Number Generator Service.
 * @author Blaise Mugisha
 * @version 1.0
 */
@Service
public class NumberGeneratorService {
    /**
     * Random number generator
     */
    Random random = new Random();
    /**
     *
     * Generate Random 2D Array
     * Generates a 2D array with random numbers within a specified range
     *
     * @param rows Number of rows
     * @param cols Number of columns
     * @param min  Minimum value (inclusive)
     * @param max  Maximum value (exclusive)
     * @return 2D array filled with random numbers in specified range
     */
    public  int[][] generateRandom2DArray(int rows, int cols, int min, int max) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be positive integers");
        }
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        int[][] array = new int[rows][cols];


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = random.nextInt(max - min + 1) + min;

            }
        }

        return array;
    }

    /**
     * Prints the 2D array to the console.
     * @param array array
     *
     */
    public  void printArray(int[][] array) {
        for (int[] row : array) {
            for (int num : row) {
                System.out.print(num + "\t");
            }
            System.out.println();
        }
    }
    /**
     *  ExtractDigits
     *
     * @param number number
     * @return int[]
     */
    public int[] extractDigits(int number) {
        int[] digits = new int[4]; // ones, tens, hundreds, thousands
        for (int i = 0; i < 4; i++) {
            digits[i] = number % 10;
            number /= 10;
        }
        return digits;
    }


    /**
     * Generate Division Questions Array
     * Creates an array of division problems based on user preferences
     *
     * @param numberOfQuestions Number of questions to generate
     * @param complexity "with-remainder" or "without-remainder"
     * @param numberOfDigits "1-digit", "2-digit", or "mixed"
     * @return Array of Maps containing division questions
     */
    public List<Map<String, Object>> generateDivisionQuestions(
            int numberOfQuestions,
            String complexity,
            String numberOfDigits) {

        if (numberOfQuestions <= 0 || numberOfQuestions > 50) {
            throw new IllegalArgumentException("Number of questions must be between 1 and 50");
        }

        List<Map<String, Object>> questions = new ArrayList<>();

        for (int i = 0; i < numberOfQuestions; i++) {
            Map<String, Object> question = new HashMap<>();

            int divisor = generateDivisor(numberOfDigits);
            int dividend = generateDividend(divisor, complexity);

            question.put("number1", dividend);  // Dividend
            question.put("number2", divisor);   // Divisor
            question.put("questionNumber", i + 1);

            // Initialize empty answer fields
            question.put("answer", 0);
            question.put("remainder", 0);
            question.put("steps", createEmptySteps());
            question.put("intermediateRemainders", createEmptyIntermediateRemainders());

            questions.add(question);
        }

        return questions;
    }

    /**
     * Generate divisor based on difficulty preference
     */
    private int generateDivisor(String numberOfDigits) {
        switch (numberOfDigits) {
            case "1-digit":
                return random.nextInt(8) + 2; // 2-9 (avoid 1 and 0)
            case "2-digit":
                return random.nextInt(90) + 10; // 10-99
            case "mixed":
                return random.nextBoolean() ?
                        random.nextInt(8) + 2 :
                        random.nextInt(90) + 10;
            default:
                return random.nextInt(8) + 2;
        }
    }

    /**
     * Generate dividend based on divisor and complexity
     */
    private int generateDividend(int divisor, String complexity) {
        if ("without-remainder".equals(complexity)) {
            // Generate quotient first, then multiply to ensure no remainder
            int quotient = random.nextInt(900) + 100; // 100-999
            return quotient * divisor;
        } else {
            // Generate random dividend that may have remainder
            int minDividend = divisor * 10; // Ensure at least 2-digit quotient
            int maxDividend = 9999;
            return random.nextInt(maxDividend - minDividend + 1) + minDividend;
        }
    }

    /**
     * Create empty steps map for user input
     */
    private Map<String, Integer> createEmptySteps() {
        Map<String, Integer> steps = new HashMap<>();
        steps.put("step1", 0);
        steps.put("step2", 0);
        steps.put("step3", 0);
        return steps;
    }

    /**
     * Create empty intermediate remainders map for user input
     */
    private Map<String, Integer> createEmptyIntermediateRemainders() {
        Map<String, Integer> remainders = new HashMap<>();
        remainders.put("remainder1", 0);
        remainders.put("remainder2", 0);
        remainders.put("remainder3", 0);
        return remainders;
    }
}



