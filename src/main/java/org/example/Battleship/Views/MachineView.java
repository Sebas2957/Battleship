package org.example.Battleship.Views;

import org.example.Battleship.Models.Matrix;
import org.example.Battleship.Models.Ship;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * View for displaying the machine's board for verification purposes.
 * Only available before starting the game.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @version 1.0
 */
public class MachineView {

    private Stage stage;
    private Matrix machineBoard;
    private Matrix playerBoard;

    private final int GRID_SIZE = 300;
    private final int NUMBERS_CELL = 10;
    private final int CELL_SIZE = GRID_SIZE / NUMBERS_CELL;

    /**
     * Constructor that creates the verification view.
     *
     * @param machineBoard Machine's board
     * @param playerBoard Player's board
     * @throws IOException If resources cannot be loaded
     */
    public MachineView(Matrix machineBoard, Matrix playerBoard) throws IOException {
        this.machineBoard = machineBoard;
        this.playerBoard = playerBoard;

        stage = new Stage();
        stage.setTitle("Verificación - Tableros");
        stage.setScene(createScene());
        stage.setResizable(false);
    }

    /**
     * Creates the scene with both boards.
     *
     * @return The configured scene
     */
    private Scene createScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        // Título
        Label title = new Label("VERIFICACIÓN DE TABLEROS");
        title.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Vista solo para verificación");
        subtitle.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");

        // Contenedor de tableros
        HBox boardsContainer = new HBox(40);
        boardsContainer.setAlignment(Pos.CENTER);

        // Tablero del jugador
        VBox playerContainer = createBoardContainer("TU FLOTA", playerBoard, "#00d4ff");

        // Tablero de la máquina
        VBox machineContainer = createBoardContainer("FLOTA ENEMIGA", machineBoard, "#e94560");

        boardsContainer.getChildren().addAll(playerContainer, machineContainer);

        // Botón cerrar
        Button closeButton = new Button("✓ CERRAR Y CONTINUAR");
        closeButton.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        closeButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, subtitle, boardsContainer, closeButton);

        return new Scene(root, 750, 500);
    }

    /**
     * Creates a container with a board and its ships.
     *
     * @param title Container title
     * @param board Board to display
     * @param color Border color
     * @return VBox with the board container
     */
    private VBox createBoardContainer(String title, Matrix board, String color) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        Label label = new Label(title);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        AnchorPane boardPane = new AnchorPane();
        boardPane.setPrefSize(GRID_SIZE, GRID_SIZE);
        boardPane.setStyle("-fx-background-color: #0a1628; -fx-border-color: " + color + "; -fx-border-width: 2;");

        // Dibujar cuadrícula
        drawGrid(boardPane);

        // Dibujar barcos
        drawShips(boardPane, board);

        container.getChildren().addAll(label, boardPane);
        return container;
    }

    /**
     * Draws the board grid.
     *
     * @param pane Pane to draw on
     */
    private void drawGrid(AnchorPane pane) {
        for (int i = 0; i <= NUMBERS_CELL; i++) {
            Line hLine = new Line(0, i * CELL_SIZE, GRID_SIZE, i * CELL_SIZE);
            hLine.setStroke(Color.web("#3a3a5a"));
            hLine.setStrokeWidth(0.5);

            Line vLine = new Line(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE);
            vLine.setStroke(Color.web("#3a3a5a"));
            vLine.setStrokeWidth(0.5);

            pane.getChildren().addAll(hLine, vLine);
        }
    }

    /**
     * Draws the ships on the board.
     *
     * @param pane Pane to draw on
     * @param board Board containing the ships
     */
    private void drawShips(AnchorPane pane, Matrix board) {
        for (int i = 0; i < 10; i++) {
            Ship ship = board.getShip(i);
            Path path = ship.getDraw();

            // Escalar el barco para el tamaño reducido
            double scale = 0.75;
            path.setScaleX(scale);
            path.setScaleY(scale);

            path.setLayoutX(ship.getTailX() * CELL_SIZE);
            path.setLayoutY(ship.getTailY() * CELL_SIZE);
            path.setStrokeWidth(1.5);
            path.setStroke(Color.web("#00d4ff"));
            path.setFill(Color.rgb(0, 212, 255, 0.1));

            if (ship.getDirection() == Ship.Direction.VERTICAL) {
                Rotate rotate = new Rotate(90, 15, 15);
                path.getTransforms().add(rotate);
            }

            pane.getChildren().add(path);
        }
    }

    /**
     * Shows the verification window.
     */
    public void show() {
        stage.show();
    }
}