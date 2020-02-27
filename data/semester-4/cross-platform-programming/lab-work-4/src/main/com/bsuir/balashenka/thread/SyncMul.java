package main.com.bsuir.balashenka.thread;

/**
 * Class of synchronized calculate.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-04-11
 */
public class SyncMul {
    private float answer;

    /**
     * Method that multiplies vectors.
     *
     * @param vectorA vector #1
     * @param vectorB vector #2
     * @return boolean
     */
    public synchronized boolean mulVectors(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) return false;
        for (int i = 0; i < vectorA.length; i++) {
            this.answer += vectorA[i] * vectorB[i];
        }
        return true;
    }

    public synchronized float getAnswer() {
        return this.answer;
    }
}