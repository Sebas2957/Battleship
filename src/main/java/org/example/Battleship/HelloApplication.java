package org.example.Battleship;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 730, 734);
        stage.setTitle("Battleship");
        stage.setScene(scene);
        // Ajustar ventana al tamaño de la escena y centrarla en pantalla
        stage.sizeToScene();
        stage.centerOnScreen();
        // Opcional: evitar que el usuario cambie el tamaño si causa problemas de diseño
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}