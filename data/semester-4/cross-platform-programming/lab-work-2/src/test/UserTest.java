package test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class of User.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */
public class UserTest {
    /**
     * Property - User class.
     */
    private User user = new User();
    /**
     * Property - Computer class.
     */
    private Computer computer = new Computer();

    /**
     * Check download application.
     */
    @Test
    public void downloadApplication() {
        assertEquals(true, user.downloadApplication(computer,
                "Firefox", "Mozila", "Browser"));
    }

    /**
     * Check delete application.
     */
    @Test
    public void deleteApplication() {
        computer.addBrowserComputer(new Browser("Firefox", "Mozila"));
        assertEquals(true, user.deleteApplicationUser(computer,
                "Firefox", "Mozila", "Browser"));
    }

    @Test
    public void installApplication() {
        computer.addBrowserComputer(new Browser("Firefox", "Mozila"));
        assertEquals(true, user.installApplication(computer,
                "Firefox", "Mozila", "Browser"));
    }

}