package org.example.Battleship.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * View for ship placement before starting the game.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class PlacementView {

    private Stage stage;

    public PlacementView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Battleship/placement-view.fxml"));
        Parent root = loader.load();

        stage = new Stage();
        stage.setTitle("Batalla Naval - Coloca tus barcos");
        stage.setScene(new Scene(root, 1337, 705));
        stage.setResizable(false);
    }

    /**
     * Overloaded constructor that sets an owner for proper centering.
     *
     * @param owner The owner Stage to which this window should be attached
     * @throws IOException If FXML file cannot be loaded
     */
    public PlacementView(Stage owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Battleship/placement-view.fxml"));
        Parent root = loader.load();

        stage = new Stage();
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }
        stage.setTitle("Batalla Naval - Coloca tus barcos");
        stage.setScene(new Scene(root, 1337, 705));
        stage.setResizable(false);
    }

    public void show() {
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }
}