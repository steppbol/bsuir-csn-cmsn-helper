package test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class of test computer.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public class ComputerTest {
    /**
     * Property - Computer class.
     */
    private Computer computer = new Computer();
    /**
     * Property - logic class.
     */
    private User user = new User();

    /**
     * Check delete application
     */
    @Test
    public void deleteApplicationComputer() {
        computer.addBrowserComputer(new Browser("Firefox", "Mozila", "D:/Programm"));
        assertEquals(true, computer.deleteApplicationComputer("Firefox",
                "Mozila", "Browser"));
    }

    /**
     * Check install application
     */
    @Test
    public void installApplicationComputer() {
        computer.addBrowserComputer(new Browser("Firefox", "Mozila", "D:/Programm"));
        assertEquals(true, computer.installApplicationComputer("Firefox",
                "Mozila", "Browser"));
    }
}