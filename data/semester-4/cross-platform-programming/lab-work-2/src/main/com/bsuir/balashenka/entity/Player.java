package main.com.bsuir.balashenka.entity;

/**
 * Class of Player.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */
public class Player extends Program {
    /**
     * Watch film.
     * @param name - name of film
     * @param author - author of film
     * @return name of film
     */
    public String watchFilm(final String name, final String author){
        return name;
    }

    /**
     * Constructor of class Player.
     */
    public Player() {
    }

    /**
     * Constructor of class Player.
     * @param name - name of film
     * @param author - author of film
     */
    public Player(final String name, final String author) {
        setNameOfProgram(name);
        setAuthorOfProgram(author);
        setStatusOfProgram(false);
    }
}
