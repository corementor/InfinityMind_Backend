package io.corementor.infinitymind.service;

import io.corementor.infinitymind.dto.DivisionQuestion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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


    public List<DivisionQuestion> generateDivisionQuestions(int count, int min, int max) {
        List<DivisionQuestion> questions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int number1 = random.nextInt(max - min + 1) + min;
            int number2 = random.nextInt(max - min + 1) + min;

            while (number2 == 0) {
                number2 = random.nextInt(max - min + 1) + min;
            }

            questions.add(new DivisionQuestion(number1, number2));
        }

        return questions;
    }



}
