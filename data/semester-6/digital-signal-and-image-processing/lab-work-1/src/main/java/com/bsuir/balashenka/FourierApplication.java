package com.bsuir.balashenka;

import com.aquafx_project.AquaFx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FourierApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        URL fxmlURL = getClass().getClassLoader().getResource("main.fxml");
        if (fxmlURL != null) {
            try {
                Parent root = FXMLLoader.load(fxmlURL);
                primaryStage.setTitle("Fourier transform");
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
                AquaFx.style();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Can not load fxml file");
        }
    }
}
