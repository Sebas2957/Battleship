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
 * @author Pablo Arias
 * @version 1.0
 */
public class GameView {

    private Stage stage;
    private Matrix machineBoard;
    private Matrix playerBoard;

    public GameView(Matrix machineBoard, Matrix playerBoard) throws IOException {
        this.machineBoard = machineBoard;
        this.playerBoard = playerBoard;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Battleship/game-view.fxml"));
        Parent root = loader.load();

        GameController controller = loader.getController();
        controller.setBoards(machineBoard, playerBoard);

        stage = new Stage();
        stage.setTitle("Batalla Naval - Juego");
        stage.setScene(new Scene(root, 1000, 520));
        stage.setResizable(false);
    }

    public void show() {
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }
}