package org.example.Battleship.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class InformationView {

    private static InformationView instance;
    private Stage stage;

    /**
     * Private constructor for Singleton pattern.
     *
     * @throws IOException If FXML file cannot be loaded
     */
    private InformationView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Battleship/information-view.fxml"));
        Parent root = loader.load();

        stage = new Stage();
        stage.setTitle("Batalla Naval");
        // Tamaño: 734x730 para coincidir con information-view.fxml
        stage.setScene(new Scene(root, 734, 730));
        stage.setResizable(false);
    }

    /**
     * Gets the singleton instance of InformationView.
     *
     * @return The single instance
     * @throws IOException If FXML file cannot be loaded
     */
    public static InformationView getInstance() throws IOException {
        if (instance == null) {
            instance = new InformationView();
        }
        return instance;
    }

    /**
     * Shows the main menu window.
     */
    public void show() {
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }
}