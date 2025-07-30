package io.corementor.mindexpanse.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class NumberGeneratorService {

   /*
   public static void main(String[] args) {
        /Random random = new Random();
        int array[][] = new int[2][3];

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = random.nextInt(10);
                System.out.print(array[i][j] + "\t");
            }
            System.out.println();
        }

        int counter = 0;
        int sum = 0;
        int min = array[0][0];
        int max = array[0][0];


        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (min > array[i][j]) {
                    min = array[i][j];
                }
                if (max < array[i][j]) {
                    max = array[i][j];
                }

                //calculate the sum and average

                sum = sum + array[i][j];
                counter++;
            }
        }

        System.out.println("Minimum number is: "+min);
        System.out.println("Maximum number is: "+max);
        System.out.println("Average number is: "+sum/counter);


        int[][] test = generateRandom2DArray(2, 3, 1, 10);
        printArray(test);*//*
    }*/

    /**
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
        Random random = new Random();

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
     *  extractDigits
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


}
