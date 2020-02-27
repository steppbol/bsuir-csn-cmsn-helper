package main.com.bsuir.balashenka.entity;

/**
 * Class of User.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */
public class User {

    /**
     * Download application by user.
     * @param computer - class Computer
     * @param name - name of program
     * @param author - author of program
     * @param type - type of application
     * @return true or false
     */
    public boolean downloadApplication(final Computer computer, final String name,
                                       final String author, final String type) {
        if (type.equals("Browser")) {
            for (Browser browser : computer.getBrowsers()) {
                if (browser.getNameOfProgram().equals(name) && browser.getAuthorOfProgram().equals(author)) {
                    return false;
                }
            }
            computer.addBrowserComputer(new Browser(name, author));
        } else {
            for (Player player : computer.getPlayers()) {
                if (player.getNameOfProgram().equals(name) && player.getAuthorOfProgram().equals(author)) {
                    return false;
                }
            }
            computer.addPlayerComputer(new Player(name, author));
        }
        return true;
    }

    /**
     * Install application by user.
     * @param computer - class Computer
     * @param name - name of program
     * @param author - author of program
     * @param type - type of application
     * @return true of false
     */
    public boolean installApplication(final Computer computer,  final String name, final String author, final String type) {
            return computer.installApplicationComputer(name, author, type);
    }

    /**
     * Use application
     * @param name - name of program
     * @param author - author of program
     * @return true or false
     */
    public String useApplication(final String name, final String author) {
        return new Player().watchFilm(name, author);
    }

    /**
     * Delete program
     * @param computer - class of Computer
     * @param name - name of program
     * @param author - author of program
     * @param type - type of application
     * @return true or false
     */
    public boolean deleteApplicationUser(final Computer computer, final String name, final String author, final String type) {
        return computer.deleteApplicationComputer(name, author, type);
    }
}
