import com.aquafx_project.AquaFx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CorrelationApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("window.fxml"));
            Scene scene = new Scene(root, 855, 730);
            scene.getStylesheets().addAll(getClass().getResource("style.css").toString());
            primaryStage.setTitle("Correlation And Convolution");
            primaryStage.setScene(scene);
            primaryStage.setMaxHeight(730);
            primaryStage.setMaxWidth(855);
            primaryStage.setMinHeight(730);
            primaryStage.setMinWidth(855);
            primaryStage.show();
            AquaFx.style();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
