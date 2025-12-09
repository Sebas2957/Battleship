package org.example.Battleship;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación Battleship.
 * Combina el lanzador y la aplicación JavaFX.
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("information-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Battleship");
        stage.setScene(scene);
        stage.show();
    }

    // El main integrado - ya no necesitas Launcher separado
    public static void main(String[] args) {
        launch(args);
    }
}