package org.example.Battleship.Controllers;

import org.example.Battleship.Models.Game;
import org.example.Battleship.Models.Matrix;
import org.example.Battleship.Models.Ship;
import org.example.Battleship.Models.utilities.serialization;
import org.example.Battleship.Views.InformationView;
import org.example.Battleship.Views.PlacementView;
import org.example.Battleship.Views.AlertBox;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

/**
 * Main game controller for Battleship.
 * Handles game logic, turns, shots and auto-save.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @version 1.0
 */
public class GameController {

    @FXML private Label labelTurnInGame;
    @FXML private AnchorPane columnsPane;
    @FXML private AnchorPane panePosition;
    @FXML private AnchorPane rowsPane;
    @FXML private AnchorPane columnsPaneMachine;
    @FXML private AnchorPane panePositionMachine;
    @FXML private AnchorPane rowsPaneMachine;
    @FXML private Label informationLabel;
    @FXML private Label nameLabel;

    private Matrix machineBoard;
    private Matrix playerBoard;
    private serialization serialization;
    private Game game;

    private final int GRID_SIZE = 400;
    private final int NUMBERS_CELL = 10;
    private final int CELL_SIZE = GRID_SIZE / NUMBERS_CELL;

    /**
     * Sets the game boards and loads any previous game if necessary.
     *
     * @param machineBoard Machine's board
     * @param playerBoard Player's board
     */
    public void setBoards(Matrix machineBoard, Matrix playerBoard) {
        this.machineBoard = machineBoard;
        this.playerBoard = playerBoard;
        game = new Game();
        serialization = new serialization();

        panePositionMachine.setOnMousePressed(this::handleMousePressed);

        this.drawGrid();
        this.drawShips();
        this.drawGridMachine();
        this.drawHitsContinue();

        nameLabel.setText(playerBoard.getUsername());
        updateTurnLabel();
    }

    /**
     * Handles the new game button action.
     *
     * @param event Action event
     */
    @FXML
    void OnActionNewGame(ActionEvent event) {
        AlertBox alertBox = new AlertBox();
        boolean confirmed = alertBox.showConfirmation(
                "Confirmación",
                "¿Estás seguro que quieres comenzar un nuevo juego?\n" +
                        "(Se perderá el progreso actual)"
        );

        if (confirmed) {
            try {
                serialization.clearFile(serialization.getRelativePath());
                InformationView infoView = InformationView.getInstance();
                infoView.show();
                ((Stage) nameLabel.getScene().getWindow()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles player shots on the machine's board.
     *
     * @param event Mouse event
     */
    public void handleMousePressed(MouseEvent event) {
        if (game.getTurn() % 2 != 0) {
            return; // No es turno del jugador
        }

        int x = (int) event.getX() / CELL_SIZE;
        int y = (int) event.getY() / CELL_SIZE;

        // Validar que no se dispare en la misma casilla
        if (machineBoard.isWaterHitOrSunk(x, y)) {
            new AlertBox().showAlert(
                    "Error",
                    "Disparo inválido",
                    "No puedes disparar 2 veces en el mismo lugar",
                    Alert.AlertType.ERROR
            );
            return;
        }

        // Procesar disparo
        if (machineBoard.getState(x, y) == Matrix.State.EMPTY) {
            // Disparo al agua, cambiar turno
            game.setTurn();
            machineBoard.changeState(x, y, Matrix.State.WATER);

            Path waterHit = drawWaterHit();
            waterHit.setLayoutX(x * CELL_SIZE);
            waterHit.setLayoutY(y * CELL_SIZE);
            panePositionMachine.getChildren().add(waterHit);

            updateTurnLabel();
            saveGameState();

            // Turno de la máquina
            machineTurn();

        } else if (machineBoard.getState(x, y) == Matrix.State.OCCUPIED) {
            // Tocado o hundido, el jugador vuelve a disparar
            machineBoard.updateShipStateToHit(x, y);
            machineBoard.updateAndCheckShipStateToSunk();

            informationLabel.setText("¡Has hundido " + machineBoard.getSunkShips() + " barcos!");
            updateTurnLabel();

            // Redibujar el tablero con los nuevos estados
            redrawMachineBoard();
            saveGameState();

            // Verificar victoria
            if (machineBoard.allShipsSunk()) {
                handleGameEnd(true);
            }
        }
    }

    /**
     * Executes the machine's turn with random shots.
     */
    private void machineTurn() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.4));
        pause.setOnFinished(e -> {
            int w, z;

            do {
                Random random = new Random();
                int BOARD_SIZE = 10;
                w = random.nextInt(BOARD_SIZE);
                z = random.nextInt(BOARD_SIZE);
            } while (playerBoard.isWaterHitOrSunk(w, z));

            if (playerBoard.getState(w, z) == Matrix.State.EMPTY) {
                // Disparo al agua, cambiar turno
                game.setTurn();
                playerBoard.changeState(w, z, Matrix.State.WATER);

                Path waterHit = drawWaterHit();
                waterHit.setLayoutX(w * CELL_SIZE);
                waterHit.setLayoutY(z * CELL_SIZE);
                panePosition.getChildren().add(waterHit);

                updateTurnLabel();
                saveGameState();

            } else if (playerBoard.getState(w, z) == Matrix.State.OCCUPIED) {
                // Tocado o hundido, la máquina vuelve a disparar
                playerBoard.updateShipStateToHit(w, z);
                playerBoard.updateAndCheckShipStateToSunk();

                updateTurnLabel();
                redrawPlayerBoard();
                saveGameState();

                // Verificar derrota
                if (playerBoard.allShipsSunk()) {
                    handleGameEnd(false);
                    return;
                }

                // La máquina vuelve a disparar
                machineTurn();
            }
        });

        pause.play();
    }

    /**
     * Saves the current game state to files.
     */
    private void saveGameState() {
        try {
            // Guardar tableros serializados
            serialization.serializeObjects("objectsSerialization.txt", machineBoard, playerBoard);

            // Guardar estadísticas en archivo plano
            serialization.savePlayerStats(
                    playerBoard.getUsername(),
                    playerBoard.getSunkShips(),
                    machineBoard.getSunkShips()
            );

            System.out.println("Estado del juego guardado automáticamente.");
        } catch (Exception e) {
            System.err.println("Error al guardar el estado del juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the end of the game.
     *
     * @param playerWins true if player won, false if machine won
     */
    private void handleGameEnd(boolean playerWins) {
        try {
            serialization.clearFile(serialization.getRelativePath());
        } catch (Exception ignored) {}

        Platform.runLater(() -> {
            String title = playerWins ? "¡GANASTE!" : "¡PERDISTE!";
            String message = playerWins
                    ? "¡Felicidades! Has hundido la flota enemiga."
                    : "La máquina ha hundido tu flota.";

            new AlertBox().showAlert(
                    title,
                    message,
                    "",
                    Alert.AlertType.INFORMATION
            );

            Platform.exit();
        });
    }

    /**
     * Updates the turn indicator label.
     */
    private void updateTurnLabel() {
        if (game.getTurn() % 2 == 0) {
            labelTurnInGame.setText("TURNO DE: " + playerBoard.getUsername());
        } else {
            labelTurnInGame.setText("TURNO DE: Máquina");
        }
    }

    /**
     * Redraws the machine's board with updated states.
     */
    private void redrawMachineBoard() {
        panePositionMachine.getChildren().removeIf(node ->
                node instanceof Path && ("shipHit".equals(node.getId()) || "shipSunk".equals(node.getId()))
        );

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (machineBoard.getState(i, j) == Matrix.State.SUNK) {
                    Path shipSunk = drawShipSunk();
                    shipSunk.setId("shipSunk");
                    shipSunk.setLayoutX(i * CELL_SIZE);
                    shipSunk.setLayoutY(j * CELL_SIZE);
                    panePositionMachine.getChildren().add(shipSunk);
                } else if (machineBoard.getState(i, j) == Matrix.State.HIT) {
                    Path shipHit = drawShipHit();
                    shipHit.setId("shipHit");
                    shipHit.setLayoutX(i * CELL_SIZE);
                    shipHit.setLayoutY(j * CELL_SIZE);
                    panePositionMachine.getChildren().add(shipHit);
                }
            }
        }
    }

    /**
     * Redraws the player's board with updated states.
     */
    private void redrawPlayerBoard() {
        panePosition.getChildren().removeIf(node ->
                node instanceof Path && ("shipHit".equals(node.getId()) || "shipSunk".equals(node.getId()))
        );

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (playerBoard.getState(i, j) == Matrix.State.SUNK) {
                    Path shipSunk = drawShipSunk();
                    shipSunk.setId("shipSunk");
                    shipSunk.setLayoutX(i * CELL_SIZE);
                    shipSunk.setLayoutY(j * CELL_SIZE);
                    panePosition.getChildren().add(shipSunk);
                } else if (playerBoard.getState(i, j) == Matrix.State.HIT) {
                    Path shipHit = drawShipHit();
                    shipHit.setId("shipHit");
                    shipHit.setLayoutX(i * CELL_SIZE);
                    shipHit.setLayoutY(j * CELL_SIZE);
                    panePosition.getChildren().add(shipHit);
                }
            }
        }
    }

    /**
     * Draws the player's board grid with labels.
     */
    public void drawGrid() {
        for (int i = 0; i <= NUMBERS_CELL; i++) {
            Line hLine = new Line(0, i * CELL_SIZE, GRID_SIZE, i * CELL_SIZE);
            hLine.setStroke(Color.web("#b4b4ff"));
            hLine.setStrokeWidth(0.5);
            panePosition.getChildren().add(hLine);

            Line vLine = new Line(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE);
            vLine.setStroke(Color.web("#b4b4ff"));
            vLine.setStrokeWidth(0.5);
            panePosition.getChildren().add(vLine);
        }

        // Etiquetas de columnas (A-J)
        for (int i = 0; i < NUMBERS_CELL; i++) {
            char letter = (char) (65 + i);
            Label label = new Label(String.valueOf(letter));
            label.setPrefSize(CELL_SIZE, 30);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            AnchorPane.setLeftAnchor(label, i * (double) CELL_SIZE);
            AnchorPane.setTopAnchor(label, 0.0);
            columnsPane.getChildren().add(label);
        }

        // Etiquetas de filas (1-10)
        for (int i = 0; i < NUMBERS_CELL; i++) {
            Label label = new Label(String.valueOf(i + 1));
            label.setPrefSize(30, CELL_SIZE);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            AnchorPane.setLeftAnchor(label, 0.0);
            AnchorPane.setTopAnchor(label, i * (double) CELL_SIZE);
            rowsPane.getChildren().add(label);
        }
    }

    /**
     * Draws the machine's board grid with labels.
     */
    public void drawGridMachine() {
        for (int i = 0; i <= NUMBERS_CELL; i++) {
            Line hLine = new Line(0, i * CELL_SIZE, GRID_SIZE, i * CELL_SIZE);
            hLine.setStroke(Color.web("#ff6b6b"));
            hLine.setStrokeWidth(0.5);
            panePositionMachine.getChildren().add(hLine);

            Line vLine = new Line(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE);
            vLine.setStroke(Color.web("#ff6b6b"));
            vLine.setStrokeWidth(0.5);
            panePositionMachine.getChildren().add(vLine);
        }

        // Etiquetas de columnas (A-J)
        for (int i = 0; i < NUMBERS_CELL; i++) {
            char letter = (char) (65 + i);
            Label label = new Label(String.valueOf(letter));
            label.setPrefSize(CELL_SIZE, 30);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            AnchorPane.setLeftAnchor(label, i * (double) CELL_SIZE);
            AnchorPane.setTopAnchor(label, 0.0);
            columnsPaneMachine.getChildren().add(label);
        }

        // Etiquetas de filas (1-10)
        for (int i = 0; i < NUMBERS_CELL; i++) {
            Label label = new Label(String.valueOf(i + 1));
            label.setPrefSize(30, CELL_SIZE);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            AnchorPane.setLeftAnchor(label, 0.0);
            AnchorPane.setTopAnchor(label, i * (double) CELL_SIZE);
            rowsPaneMachine.getChildren().add(label);
        }
    }

    /**
     * Draws the player's ships on the board.
     */
    public void drawShips() {
        for (int i = 0; i < 10; i++) {
            Ship ship = playerBoard.getShip(i);
            Path path = ship.getDraw();

            path.setLayoutX(ship.getTailX() * CELL_SIZE);
            path.setLayoutY(ship.getTailY() * CELL_SIZE);
            path.setStrokeWidth(2);
            path.setStroke(Color.web("#00d4ff"));
            path.setFill(Color.rgb(0, 212, 255, 0.15));

            if (ship.getDirection() == Ship.Direction.VERTICAL) {
                Rotate rotate = new Rotate(90, 20, 20);
                path.getTransforms().add(rotate);
            }

            panePosition.getChildren().add(path);
        }
    }

    /**
     * Draws an X shape to indicate a water shot.
     *
     * @return Path with the water figure
     */
    public static Path drawWaterHit() {
        Path path = new Path();
        int size = 40;
        int padding = 8;

        // Línea diagonal \
        path.getElements().add(new MoveTo(padding, padding));
        path.getElements().add(new LineTo(size - padding, size - padding));

        // Línea diagonal /
        path.getElements().add(new MoveTo(size - padding, padding));
        path.getElements().add(new LineTo(padding, size - padding));

        path.setStroke(Color.web("#4a9eff"));
        path.setStrokeWidth(3);
        path.setStrokeLineCap(StrokeLineCap.ROUND);

        return path;
    }

    /**
     * Draws a circle to indicate a hit ship.
     *
     * @return Path with the hit figure
     */
    public static Path drawShipHit() {
        Path path = new Path();
        int size = 40;
        int centerX = size / 2;
        int centerY = size / 2;
        int radius = 12;

        // Crear círculo usando arcos
        path.getElements().add(new MoveTo(centerX + radius, centerY));
        path.getElements().add(new ArcTo(radius, radius, 0, centerX - radius, centerY, false, true));
        path.getElements().add(new ArcTo(radius, radius, 0, centerX + radius, centerY, false, true));
        path.getElements().add(new ClosePath());

        path.setStroke(Color.web("#ffa500"));
        path.setStrokeWidth(2);
        path.setFill(Color.web("#ffa500", 0.6));

        return path;
    }

    /**
     * Draws a circle with X to indicate a sunk ship.
     *
     * @return Path with the sunk figure
     */
    public static Path drawShipSunk() {
        Path path = new Path();
        int size = 40;
        int centerX = size / 2;
        int centerY = size / 2;
        int radius = 14;
        int padding = 6;

        // Círculo exterior
        path.getElements().add(new MoveTo(centerX + radius, centerY));
        path.getElements().add(new ArcTo(radius, radius, 0, centerX - radius, centerY, false, true));
        path.getElements().add(new ArcTo(radius, radius, 0, centerX + radius, centerY, false, true));

        // X interior
        path.getElements().add(new MoveTo(centerX - padding, centerY - padding));
        path.getElements().add(new LineTo(centerX + padding, centerY + padding));
        path.getElements().add(new MoveTo(centerX + padding, centerY - padding));
        path.getElements().add(new LineTo(centerX - padding, centerY + padding));

        path.setStroke(Color.web("#ff4444"));
        path.setStrokeWidth(2.5);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setFill(Color.web("#ff4444", 0.3));

        return path;
    }

    /**
     * Redraws hit states when loading a saved game.
     */
    public void drawHitsContinue() {
        // Redibujar estados del tablero del jugador
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Matrix.State state = playerBoard.getState(i, j);
                if (state == Matrix.State.WATER) {
                    Path water = drawWaterHit();
                    water.setLayoutX(i * CELL_SIZE);
                    water.setLayoutY(j * CELL_SIZE);
                    panePosition.getChildren().add(water);
                } else if (state == Matrix.State.HIT) {
                    Path hit = drawShipHit();
                    hit.setLayoutX(i * CELL_SIZE);
                    hit.setLayoutY(j * CELL_SIZE);
                    panePosition.getChildren().add(hit);
                } else if (state == Matrix.State.SUNK) {
                    Path sunk = drawShipSunk();
                    sunk.setLayoutX(i * CELL_SIZE);
                    sunk.setLayoutY(j * CELL_SIZE);
                    panePosition.getChildren().add(sunk);
                }
            }
        }

        // Redibujar estados del tablero de la máquina
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Matrix.State state = machineBoard.getState(i, j);
                if (state == Matrix.State.WATER) {
                    Path water = drawWaterHit();
                    water.setLayoutX(i * CELL_SIZE);
                    water.setLayoutY(j * CELL_SIZE);
                    panePositionMachine.getChildren().add(water);
                } else if (state == Matrix.State.HIT) {
                    Path hit = drawShipHit();
                    hit.setLayoutX(i * CELL_SIZE);
                    hit.setLayoutY(j * CELL_SIZE);
                    panePositionMachine.getChildren().add(hit);
                } else if (state == Matrix.State.SUNK) {
                    Path sunk = drawShipSunk();
                    sunk.setLayoutX(i * CELL_SIZE);
                    sunk.setLayoutY(j * CELL_SIZE);
                    panePositionMachine.getChildren().add(sunk);
                }
            }
        }
    }
}