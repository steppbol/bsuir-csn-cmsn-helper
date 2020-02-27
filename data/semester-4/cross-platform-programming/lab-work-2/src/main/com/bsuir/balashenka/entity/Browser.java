package main.com.bsuir.balashenka.entity;

/**
 * Class of Browser.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */
public class Browser extends Program {
    /**
     * Method that open web-page.
     * @return true;
     */

    public boolean openWebpage() {
        return true;
    }
    /**
     * Method that download File.
     * @return true;
     */

    public boolean downloadFile() {
        return true;
    }

    /**
     * Constructor of class Browser.
     */
    public Browser() {
    }

    /**
     * Constructor of class Browser.
     * @param name - name of browser
     * @param author - author of browser
     */
    public Browser(final String name, final String author) {
        setNameOfProgram(name);
        setAuthorOfProgram(author);
        setStatusOfProgram(false);
    }
}
