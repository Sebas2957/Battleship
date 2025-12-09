package org.example.Battleship.Views;

import org.example.Battleship.Controllers.GameController;
import org.example.Battleship.Models.Matrix;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main game view where shots are fired.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @version 1.0
 */
public class GameView {

    private Stage stage;
    private Matrix machineBoard;
    private Matrix playerBoard;

    /**
     * Constructor that creates the game view.
     *
     * @param machineBoard Machine's board
     * @param playerBoard Player's board
     * @throws IOException If FXML file cannot be loaded
     */
    public GameView(Matrix machineBoard, Matrix playerBoard) throws IOException {
        this.machineBoard = machineBoard;
        this.playerBoard = playerBoard;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Battleship/game-view.fxml"));
        Parent root = loader.load();

        // Pasar los tableros al controlador
        GameController controller = loader.getController();
        controller.setBoards(machineBoard, playerBoard);

        stage = new Stage();
        stage.setTitle("Batalla Naval - Juego");
        stage.setScene(new Scene(root, 900, 500));
        stage.setResizable(false);
    }

    /**
     * Shows the game window.
     */
    public void show() {
        stage.show();
    }
}