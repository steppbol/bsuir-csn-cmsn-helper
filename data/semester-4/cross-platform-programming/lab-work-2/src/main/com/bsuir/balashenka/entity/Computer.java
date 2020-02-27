package main.com.bsuir.balashenka.entity;

import java.util.ArrayList;

/**
 * Class of Computer.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */
public class Computer {

    /**Property - ArrayList of browsers.*/
    private ArrayList<Browser> browsers = new ArrayList<>();
    /**Property - ArrayList of players.*/
    private ArrayList<Player> players = new ArrayList<>();

    /**
     * Constructor of class Computer.
     * Add one browser and player in collection.
     */
    public Computer() {
        browsers.add(new Browser("Internet Explorer", "Microsoft"));
        players.add(new Player("Groove", "Microsoft"));
    }

    /**
     * Method that delete application.
     * @param name - name of program
     * @param author - author of program
     * @param type - type of application
     * @return true or false
     */
    public boolean deleteApplicationComputer(final String name, final String author, final String type) {
        boolean check = false;
        if (type.equals("Browser")) {
            for (Browser browser : browsers) {
                if (browser.getNameOfProgram().equals(name) && browser.getAuthorOfProgram().equals(author)) {
                    browsers.remove(browser);
                    check = true;
                    break;
                }
            }
        } else{

            for (Player player : players) {
                if (player.getNameOfProgram().equals(name) && player.getAuthorOfProgram().equals(author)) {
                    players.remove(player);
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    /**
     * Method that install application.
     * @param name - name of program
     * @param author - author of program
     * @param type - type of application
     * @return true or false
     */
    public boolean installApplicationComputer(final String name, final String author, final String type) {
        boolean check = false;
        if (type.equals("Browser")) {
            for (Browser browser : browsers) {
                if (browser.getNameOfProgram().equals(name) && browser.getAuthorOfProgram().equals(author)) {
                    browser.setStatusOfProgram(true);
                    check = true;
                    break;
                }
            }

        } else {
            for (Player player : players) {
                if (player.getNameOfProgram().equals(name) && player.getAuthorOfProgram().equals(author)) {
                    player.setStatusOfProgram(true);
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    /**
     * Add browser in collection.
     * @param browser - browser
     */
    public void addBrowserComputer(final Browser browser) {
            browsers.add(browser);
    }

    /**
     * Add player in collection.
     * @param player - browser
     */
    public void addPlayerComputer(final Player player){
            players.add(player);
    }

    /**
     * @return ArrayList
     */
    public ArrayList<Browser> getBrowsers() {
        return browsers;
    }

    /**
     * Set collection.
     * @param browsersTemp - collection
     */
    public void setBrowsers(final ArrayList<Browser> browsersTemp) {
        this.browsers = browsersTemp;
    }

    /**
     * @return ArrayList
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Set collection.
     * @param playersTemp - collection
     */
    public void setPlayers(final ArrayList<Player> playersTemp) {
        this.players = playersTemp;
    }
}
