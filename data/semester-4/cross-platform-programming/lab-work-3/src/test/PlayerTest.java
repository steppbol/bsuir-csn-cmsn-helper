package test;

import com.home.labwork2.logic.action.Player;
import com.home.labwork2.logic.action.Videofile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class of test Player.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-29
 */
public class PlayerTest {

    /**
     * Check watching film.
     */
    @Test
    public void watchFilm() {
        assertEquals("Wrong format", new Player().watchFilm(new Videofile(".mp5",
                "15:2", "Seven", "D:/Film")));
    }
}