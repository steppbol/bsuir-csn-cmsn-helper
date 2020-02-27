package main.com.bsuir.balashenka.thread;

/**
 * Class of main thread, that create other threads.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-04-11
 */
public class MainThread {

    private float matrixA[][];
    private float matrixB[][];
    private float matrixC[][];

    /**
     * Method that calculate the product of matrices.
     *
     * @param tempMatrixA matrix #1
     * @param tempMatrixB matrix #2
     * @param size        size of answer matrix
     * @param check       #1*#2(TRUE) OR #2*#1(FALSE)
     * @return answer matrix
     */
    public float[][] getComposition(float[][] tempMatrixA, float[][] tempMatrixB, int size, boolean check) {

        MulThread compositionMatrix = new MulThread();
        Thread threads[] = new Thread[0];

        float tempMatrixC[][] = new float[size][size];
        float transMatrix[][];

        if (check) {
            transMatrix = new float[tempMatrixB[0].length][tempMatrixB.length];

            for (int i = 0; i < transMatrix.length; i++) {
                for (int j = 0; j < transMatrix[0].length; j++) {
                    transMatrix[i][j] = tempMatrixB[j][i];
                }
            }

            toMul(threads, compositionMatrix, tempMatrixC, tempMatrixA, transMatrix);

        } else {
            transMatrix = new float[tempMatrixA[0].length][tempMatrixA.length];

            for (int i = 0; i < transMatrix.length; i++) {
                for (int j = 0; j < transMatrix[0].length; j++) {
                    transMatrix[i][j] = tempMatrixA[j][i];
                }
            }

            toMul(threads, compositionMatrix, tempMatrixC, tempMatrixB, transMatrix);
        }

        return tempMatrixC;
    }

    /**
     * Method that create threads.
     *
     * @param threads           array of threads
     * @param compositionMatrix class that implement Runnable
     * @param matrix            answer matrix
     * @param matrixA           matrix#1
     * @param matrixB           matrix#2
     */
    private void toMul(Thread[] threads, MulThread compositionMatrix, float[][] matrix,
                       float[][] matrixA, float[][] matrixB) {
        float answer[];
        answer = new float[2];
        threads = new Thread[matrix.length];
        for (int j = 0; j < matrixB.length; j++) {
            for (int i = 0; i < matrixB.length; i++) {
                compositionMatrix = new MulThread(matrixA[j], matrixB[i], answer, 0);

                threads[i] = new Thread(compositionMatrix);

                threads[i].start();
                matrix[j][i] = answer[0];
                answer[0] = 0;
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.out.println("ERROR#2 IN THREADS");
                }

            }
        }
    }

    /**
     * Main method of program, that start program.
     *
     * @param args args of cmd.
     */
    public static void main(String[] args) {
        GUI gui = new GUI();
        Controller controller = new Controller();
    }
}