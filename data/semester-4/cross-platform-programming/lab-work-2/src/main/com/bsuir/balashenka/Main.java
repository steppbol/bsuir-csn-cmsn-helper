package main.com.bsuir.balashenka

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The program of user's action: install, delete, download, watch.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */

/**
 * Main class that create application.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */
public class Main extends Application implements Constants {

    /**
     * Main method that create application.
     * @param primaryStage - main stage
     */
    @Override
        public void start (Stage primaryStage) throws Exception {
            GUI gui = new GUI();
            Computer computer = new Computer();
            Controller controller = new Controller(gui, computer);
            primaryStage.setTitle("User Window");
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(gui, MAX_WIDTH, MAX_HEIGHT));
            primaryStage.show();
        }

    /**
     * Main method of application.
     * @param args - args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
