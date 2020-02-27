package main.com.bsuir.balashenka.thread;

/**
 * Class of thread, that mul vector on vector.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-04-11
 */
public class MulThread implements Runnable {

    private float vectorA[];
    private float vectorB[];

    public MulThread() {

    }

    /**
     * Constructor of class, that create object of class SyncMul and calculate answer.
     *
     * @param vectorA vector #1
     * @param vectorB vector #2
     * @param answer  answer vector
     * @param index   index of cell in answer vector
     */
    public MulThread(float[] vectorA, float[] vectorB, float[] answer, int index) {
        SyncMul syncMul = new SyncMul();
        syncMul.mulVectors(vectorA, vectorB);
        answer[index] = syncMul.getAnswer();
    }

    @Override
    public void run() {
        try {
            java.lang.Thread.sleep(100);
        } catch (Exception ex) {
            System.out.println("ERROR#1 IN THREADS");
        }
    }
}