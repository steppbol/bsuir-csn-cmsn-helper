package main.com.bsuir.balashenka.interface;

/**
 * Class of logic.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public class User {
    /**
     * Name of user
     */
    private String nameOfUser;

    /**
     * Download application by user.
     *
     * @param computer class Computer
     * @param name     name of program
     * @param author   author of program
     * @param type     type of application
     * @param path     path to file
     * @return true or false
     */
    public boolean downloadApplication(final Computer computer, final String name,
                                       final String author, final String path, final String type) {
        if (type.equals("Browser")) {
            for (Browser browser : computer.getBrowsers()) {
                if (browser.getNameOfProgram().equals(name) && browser.getAuthorOfProgram().equals(author)) {
                    return false;
                }
            }
            computer.addBrowserComputer(new Browser(name, author, path));
        } else if (type.equals("Player")) {
            for (Player player : computer.getPlayers()) {
                if (player.getNameOfProgram().equals(name) && player.getAuthorOfProgram().equals(author)) {
                    return false;
                }
            }
            computer.addPlayerComputer(new Player(name, author, path));
        } else {
            return true;
        }
        return true;
    }

    /**
     * Install application by user.
     *
     * @param computer class Computer
     * @param name     name of program
     * @param author   author of program
     * @param path     path to file
     * @param type     type of application
     *                 * @param path - path to file
     * @return true of false
     */
    public boolean installApplication(final Computer computer, final String name,
                                      final String author, final String path, final String type) {
        return computer.installApplicationComputer(name, author, type);
    }

    /**
     * Use application
     *
     * @param videofile object of class Videofile
     * @return String
     */
    public String useApplication(final Videofile videofile) {
        return new Player().watchFilm(videofile);
    }

    /**
     * Delete program
     *
     * @param computer class of Computer
     * @param name     name of program
     * @param author   author of program
     * @param type     type of application
     * @param path     path to file
     * @return true or false
     */
    public boolean deleteApplicationUser(final Computer computer, final String name,
                                         final String author, final String path, final String type) {
        return computer.deleteApplicationComputer(name, author, type);
    }

    /**
     * @return name of user
     */
    public String getNameOfUser() {
        return nameOfUser;
    }

    /**
     * @param nameOfUserTemp name of user
     */
    public void setNameOfUser(final String nameOfUserTemp) {
        this.nameOfUser = nameOfUserTemp;
    }
}
