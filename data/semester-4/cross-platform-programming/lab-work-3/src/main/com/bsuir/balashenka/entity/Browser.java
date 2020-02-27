package main.com.bsuir.balashenka.interface;

import java.util.Date;

/**
 * Class of Browser.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public class Browser extends Program {

    /**
     * Constructor of class Browser.
     */
    public Browser() {
    }

    /**
     * Constructor of class Browser.
     *
     * @param name   - name of browser
     * @param author - author of browser
     * @param path   - path to file
     */
    public Browser(final String name, final String author, final String path) {
        setNameOfProgram(name);
        setAuthorOfProgram(author);
        setPathToFile(path);
        setStatusOfProgram(false);
        setDateOfCreating(new Date().toString());
    }

    /**
     * Method that open web-page.
     *
     * @return true;
     */
    public boolean openWebpage() {
        return true;
    }

    /**
     * Method that download File.
     *
     * @return true;
     */
    public boolean downloadFile() {
        return true;
    }
}
