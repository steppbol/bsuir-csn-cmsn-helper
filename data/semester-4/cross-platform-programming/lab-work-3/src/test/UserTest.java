package test;

import com.home.labwork2.logic.action.Browser;
import com.home.labwork2.logic.action.Computer;
import com.home.labwork2.logic.action.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class of test User.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public class UserTest {
    /**
     * Property - logic class.
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
                "Firefox", "Mozila", "D:/Programm", "Browser"));
    }

    /**
     * Check delete application.
     */
    @Test
    public void deleteApplication() {
        computer.addBrowserComputer(new Browser("Firefox", "Mozila", "D:/Programm"));
        assertEquals(true, user.deleteApplicationUser(computer,
                "Firefox", "Mozila", "D:/Programm", "Browser"));
    }

    /**
     * Check install application.
     */
    @Test
    public void installApplication() {
        computer.addBrowserComputer(new Browser("Firefox", "Mozila", "D:/Programm"));
        assertEquals(true, user.installApplication(computer,
                "Firefox", "Mozila", "D:/Programm", "Browser"));
    }

}