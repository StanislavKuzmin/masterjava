package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        List<Callable<int[]>> tasks = new ArrayList<>();
        for (int[] ints : matrixA) {
            tasks.add(() -> {
                int[] lines = new int[matrixSize];
                for (int j = 0; j < matrixSize; j++) {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += ints[k] * matrixB[k][j];
                    }
                    lines[j] = sum;
                }
                return lines;
            });
        }
        List<Future<int[]>> results = executor.invokeAll(tasks);
        int lineIndex = 0;
        for (Future<int[]> result : results) {
            matrixC[lineIndex] = result.get();
            lineIndex++;
        }
        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        int[] thatColumn = new int[matrixSize];
//        for (int i = 0; i < matrixSize; i++) {
//            for (int j = 0; j < matrixSize; j++) {
//                int sum = 0;
//                for (int k = 0; k < matrixSize; k++) {
//                    sum += matrixA[i][k] * matrixB[k][j];
//                }
//                matrixC[i][j] = sum;
//            }
//        }
        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    thatColumn[k] = matrixB[k][j];
                }

                for (int i = 0; i < matrixSize; i++) {
                    int[] thisRow = matrixA[i];
                    int summand = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        summand += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = summand;
                }
            }
        } catch (IndexOutOfBoundsException ignored) { }

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
