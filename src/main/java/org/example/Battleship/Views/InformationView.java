package org.example.Battleship.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main menu view (Singleton pattern).
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
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
        stage.setScene(new Scene(root, 600, 500));
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
        stage.show();
    }
}