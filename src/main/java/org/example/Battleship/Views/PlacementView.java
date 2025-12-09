package org.example.Battleship.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * View for ship placement before starting the game.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @version 1.0
 */
public class PlacementView {

    private Stage stage;

    /**
     * Constructor that creates the placement view.
     *
     * @throws IOException If FXML file cannot be loaded
     */
    public PlacementView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Battleship/placement-view.fxml"));
        Parent root = loader.load();

        stage = new Stage();
        stage.setTitle("Batalla Naval - Coloca tus barcos");
        stage.setScene(new Scene(root, 600, 550));
        stage.setResizable(false);
    }

    /**
     * Shows the placement window.
     */
    public void show() {
        stage.show();
    }
}