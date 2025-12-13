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
        stage.setScene(new Scene(root, 1337, 705));
        stage.setResizable(false);
    }

    /**
     * Overloaded constructor that sets an owner for proper centering relative to the parent window.
     * If a non-null owner Stage is passed, this stage will be initialized as a child window.
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
        stage.setScene(new Scene(root, 600, 550));
        stage.setResizable(false);
    }

    /**
     * Shows the placement window.
     */
    public void show() {
        // Ajustar al tamaño de la escena y centrar en pantalla antes/después de mostrar
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }
}