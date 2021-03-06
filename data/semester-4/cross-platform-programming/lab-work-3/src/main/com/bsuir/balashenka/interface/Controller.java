package main.com.bsuir.balashenka.interface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

import java.util.Date;

/**
 * Class of Controller. Main events of GUI's components.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public class Controller {

    /**
     * Constructor of class Controller.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     * @param user     object of class User
     */
    public Controller(final GUI gui, final Computer computer, final User user) {
        installBrowserBoxEvent(gui, computer);
        downloadBrowserBoxEvent(gui, computer);
        deleteBrowserBoxEvent(gui, computer);

        installPlayerBoxEvent(gui, computer);
        downloadPlayerBoxEvent(gui, computer);
        deletePlayerBoxEvent(gui, computer);

        watchFilmCheckBoxEvent(gui, computer);
        downloadFilmCheckBoxEvent(gui, computer);

        nameOfUserTextFieldEvent(gui, user);
        nameTextFieldEvent(gui);
        authorTextFieldEvent(gui);
        pathTextFiledEvent(gui);
        formatTextFieldEvent(gui);
    }

    /**
     * Event of TextField that contains user's name.
     *
     * @param gui  gui that contains components
     * @param user object of class User
     */
    private void nameOfUserTextFieldEvent(final GUI gui, final User user) {

        gui.nameOfUserTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                user.setNameOfUser(gui.nameOfUserTextField.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Hello window");
                alert.setHeaderText(null);
                String message = "Hello, " + gui.nameOfUserTextField.getText() + "!";
                alert.setContentText(message);

                alert.showAndWait();
            }
        });
    }

    /**
     * Event of TextField that contains name.
     *
     * @param gui gui that contains components
     */
    private void nameTextFieldEvent(final GUI gui) {

        gui.nameTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            }
        });
    }

    /**
     * Event of TextField that contains author.
     *
     * @param gui gui that contains components
     */
    private void authorTextFieldEvent(final GUI gui) {

        gui.authorTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            }
        });
    }

    /**
     * Event of TextField that contains path.
     *
     * @param gui gui that contains components
     */
    private void pathTextFiledEvent(final GUI gui) {
        gui.pathTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                gui.installBrowserButton.setDisable(false);
                gui.downloadBrowserButton.setDisable(false);
                gui.deleteBrowserButton.setDisable(false);
                gui.installPlayerButton.setDisable(false);
                gui.downloadPlayerButton.setDisable(false);
                gui.deletePlayerButton.setDisable(false);
            }
        });
    }

    /**
     * Event of TextField that contains format.
     *
     * @param gui gui that contains components
     */
    private void formatTextFieldEvent(final GUI gui) {
        gui.formatTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                gui.watchFilmButton.setDisable(false);
                gui.downloadFilmButton.setDisable(false);
            }
        });
    }

    /**
     * Event of CheckBox that install browser.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void installBrowserBoxEvent(final GUI gui, final Computer computer) {

        gui.installBrowserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();
                if (user.installApplication(computer, gui.nameTextField.getText(),
                        gui.authorTextField.getText(), gui.pathTextField.getText(), "Browser")) {
                    gui.actionComputerLabel.setText("Install");
                } else gui.actionComputerLabel.setText("Not found");

                gui.actionBrowserLabel.setText("...");
                gui.browserLabel.setText("...");
                gui.computerLabel.setText(gui.nameTextField.getText());

                gui.playerLabel.setText("...");
            }
        });
    }

    /**
     * Event of CheckBox that download browser.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void downloadBrowserBoxEvent(final GUI gui, final Computer computer) {

        gui.downloadBrowserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();
                if (user.downloadApplication(computer, gui.nameTextField.getText(),
                        gui.authorTextField.getText(), gui.pathTextField.getText(), "Browser")) {

                    Browser browser = new Browser();
                    if (browser.downloadFile()) {
                        if (browser.openWebpage()) {
                            browser = computer.getBrowsers().get(gui.ZERO);
                            gui.browserLabel.setText(browser.getNameOfProgram());
                            gui.actionBrowserLabel.setText("Download");
                            gui.actionComputerLabel.setText("...");
                            gui.computerLabel.setText("...");
                            gui.playerLabel.setText("...");
                        }
                    }
                } else {
                    Browser browser = new Browser();
                    gui.browserLabel.setText(browser.getNameOfProgram());
                    gui.actionBrowserLabel.setText("Error");
                    gui.actionComputerLabel.setText("...");
                    gui.computerLabel.setText("...");
                    gui.playerLabel.setText("...");
                }

            }
        });
    }

    /**
     * Event of CheckBox that delete browser.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void deleteBrowserBoxEvent(final GUI gui, final Computer computer) {
        gui.deleteBrowserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();
                if (computer.getBrowsers().size() == gui.ONE) {
                    gui.actionComputerLabel.setText("Error");
                } else {
                    if (user.deleteApplicationUser(computer, gui.nameTextField.getText(), gui.authorTextField.getText(),
                            gui.pathTextField.getText(), "Browser")) {
                        gui.actionComputerLabel.setText("Delete");
                    } else {
                        gui.actionComputerLabel.setText("Not found");
                    }

                    gui.browserLabel.setText("...");
                    gui.actionBrowserLabel.setText("...");
                    gui.computerLabel.setText(gui.nameTextField.getText());
                    gui.playerLabel.setText("...");
                }
            }
        });
    }

    /**
     * Event of CheckBox that install player.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void installPlayerBoxEvent(final GUI gui, final Computer computer) {

        gui.installPlayerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();
                if (user.installApplication(computer, gui.nameTextField.getText(),
                        gui.authorTextField.getText(), gui.pathTextField.getText(), "Player")) {
                    gui.actionComputerLabel.setText("Install");
                } else gui.actionComputerLabel.setText("Not found");

                gui.actionBrowserLabel.setText("...");
                gui.playerApplicationLabel.setText("...");
                gui.browserLabel.setText("...");
                gui.computerLabel.setText(gui.nameTextField.getText());
                gui.playerLabel.setText("...");
            }
        });
    }

    /**
     * Event of CheckBox that download player.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void downloadPlayerBoxEvent(final GUI gui, final Computer computer) {

        gui.downloadPlayerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();
                if (user.downloadApplication(computer, gui.nameTextField.getText(),
                        gui.authorTextField.getText(), gui.pathTextField.getText(), "Player")) {

                    Browser browser = new Browser();
                    Player player;
                    if (browser.downloadFile()) {
                        if (browser.openWebpage()) {
                            player = computer.getPlayers().get(gui.ZERO);
                            browser = computer.getBrowsers().get(gui.ZERO);
                            gui.browserLabel.setText(browser.getNameOfProgram());
                            gui.actionBrowserLabel.setText("Download");
                            gui.playerApplicationLabel.setText(player.getNameOfProgram());
                            gui.actionPlayerLabel.setText("...");
                            gui.actionComputerLabel.setText("...");
                            gui.computerLabel.setText("...");
                            gui.playerLabel.setText("...");
                        }
                    }

                } else {
                    gui.browserLabel.setText("...");
                    gui.actionBrowserLabel.setText("Error");
                    gui.playerApplicationLabel.setText("...");
                    gui.actionPlayerLabel.setText("...");
                    gui.actionComputerLabel.setText("...");
                    gui.computerLabel.setText("...");
                    gui.playerLabel.setText("...");
                }
            }
        });
    }

    /**
     * Event of CheckBox that delete player.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void deletePlayerBoxEvent(final GUI gui, final Computer computer) {
        gui.deletePlayerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();
                if (computer.getPlayers().size() == gui.ONE) {
                    gui.actionComputerLabel.setText("Error");
                } else {
                    if (user.deleteApplicationUser(computer, gui.nameTextField.getText(), gui.authorTextField.getText(),
                            gui.pathTextField.getText(), "Player")) {
                        gui.actionComputerLabel.setText("Delete");
                    } else {
                        gui.actionComputerLabel.setText("Not found");
                    }

                    gui.browserLabel.setText("...");
                    gui.actionBrowserLabel.setText("...");
                    gui.computerLabel.setText(gui.nameTextField.getText());
                    gui.playerLabel.setText("...");
                }
            }
        });
    }

    /**
     * Event of CheckBox that watch film.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void watchFilmCheckBoxEvent(final GUI gui, final Computer computer) {
        gui.watchFilmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();

                try {
                    gui.playerLabel.setText(user.useApplication(computer.getFilms().get(gui.ZERO)));
                } catch (Exception e1) {
                    gui.browserLabel.setText("...");
                    gui.actionBrowserLabel.setText("...");
                    gui.computerLabel.setText("...");
                    gui.actionComputerLabel.setText("...");
                    gui.actionPlayerLabel.setText("Not found");
                    return;
                } finally {
                    gui.browserLabel.setText("...");
                    gui.actionBrowserLabel.setText("...");
                    gui.computerLabel.setText("...");
                    gui.actionComputerLabel.setText("...");
                }

                gui.actionPlayerLabel.setText("Watch");
                Player player;
                player = computer.getPlayers().get(gui.ZERO);
                gui.playerApplicationLabel.setText(player.getNameOfProgram());
            }
        });
    }

    /**
     * Event of CheckBox that download film.
     *
     * @param gui      gui that contains components
     * @param computer class Computer
     */
    private void downloadFilmCheckBoxEvent(final GUI gui, final Computer computer) {
        gui.downloadFilmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                User user = new User();

                gui.browserLabel.setText("...");
                gui.actionBrowserLabel.setText("...");
                gui.computerLabel.setText("...");
                gui.actionComputerLabel.setText("...");
                gui.actionPlayerLabel.setText("...");
                gui.playerLabel.setText("...");
                gui.actionPlayerLabel.setText("...");
                gui.playerApplicationLabel.setText("...");

                Browser browser = new Browser();
                if (browser.downloadFile()) {
                    if (browser.openWebpage()) {
                        browser = computer.getBrowsers().get(gui.ZERO);
                        gui.browserLabel.setText(browser.getNameOfProgram());
                        gui.actionBrowserLabel.setText("Download");
                        Videofile videofile = new Videofile(gui.formatTextField.getText(),
                                new Date().toString(), gui.nameTextField.getText(), gui.pathTextField.getText());
                        computer.addFilmComputer(videofile);
                    }
                }
            }
        });
    }
}