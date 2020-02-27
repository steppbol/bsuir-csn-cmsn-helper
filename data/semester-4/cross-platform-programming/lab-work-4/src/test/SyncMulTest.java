package test;

import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Class that test methods of class SyncMul.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-04-11
 */
public class SyncMulTest {

    private SyncMul syncMul = new SyncMul();
    private float vectorA[];
    private float vectorB[];

    /**
     * Method that test vectors multiple.
     */
    @Test
    public void mulVectors() {
        this.vectorA = new float[5];
        this.vectorB = new float[5];

        for (int i = 0; i < this.vectorA.length; i++) {
            vectorA[i] = vectorB[i] = 1;
        }

        assertTrue(syncMul.mulVectors(vectorA, vectorB));
    }

    /**
     * Method that equal answer of mul vectors.
     */
    @Test
    public void getAnswer() {
        mulVectors();

        assertEquals("5.0", String.valueOf(syncMul.getAnswer()));
    }
}