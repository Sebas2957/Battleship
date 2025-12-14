package org.example.Battleship.Controllers;

import org.example.Battleship.Models.Game;
import org.example.Battleship.Models.Matrix;
import org.example.Battleship.Models.Ship;
import org.example.Battleship.Models.utilities.serialization;
import org.example.Battleship.Views.InformationView;
import org.example.Battleship.Views.AlertBox;
import javafx.animation.PauseTransition;
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
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

/**
 * Main game controller for Battleship.
 * Handles game logic, turns, shots and auto-save.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
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

    // Tamaño del tablero en píxeles (300x300 para coincidir con el FXML)
    private final int GRID_SIZE = 300;
    private final int NUMBERS_CELL = 10;
    private final int CELL_SIZE = GRID_SIZE / NUMBERS_CELL; // 30px por celda

    /**
     * Sets the game boards and initializes the view.
     *
     * @param machineBoard Machine's board
     * @param playerBoard Player's board
     */
    /**
     * Sets the game boards and initializes the view.
     *
     * @param machineBoard Machine's board
     * @param playerBoard Player's board
     */
    public void setBoards(Matrix machineBoard, Matrix playerBoard) {
        this.machineBoard = machineBoard;
        this.playerBoard = playerBoard;
        game = new Game();
        serialization = new serialization();

        // Fijar tamaño de ambos tableros para evitar expansión
        panePosition.setPrefSize(GRID_SIZE, GRID_SIZE);
        panePosition.setMaxSize(GRID_SIZE, GRID_SIZE);
        panePosition.setMinSize(GRID_SIZE, GRID_SIZE);

        panePositionMachine.setPrefSize(GRID_SIZE, GRID_SIZE);
        panePositionMachine.setMaxSize(GRID_SIZE, GRID_SIZE);
        panePositionMachine.setMinSize(GRID_SIZE, GRID_SIZE);

        // Configurar evento de clic en tablero enemigo
        panePositionMachine.setOnMousePressed(this::handleMousePressed);

        // Dibujar los tableros
        this.drawGrid();
        this.drawShips();
        this.drawGridMachine();
        this.drawHitsContinue();

        // Mostrar nombre del jugador
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
        // Verificar que sea turno del jugador (turno par)
        if (game.getTurn() % 2 != 0) {
            return;
        }

        int x = (int) event.getX() / CELL_SIZE;
        int y = (int) event.getY() / CELL_SIZE;

        // Validar límites del tablero
        if (x < 0 || x >= 10 || y < 0 || y >= 10) {
            return;
        }

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

        // Procesar disparo según el estado de la casilla
        if (machineBoard.getState(x, y) == Matrix.State.EMPTY) {
            // Disparo al agua - cambiar turno
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
            // Tocado - el jugador vuelve a disparar
            machineBoard.updateShipStateToHit(x, y);
            machineBoard.updateAndCheckShipStateToSunk();

            informationLabel.setText("¡Has hundido " + machineBoard.getSunkShips() + " barcos enemigos!");
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
        // Pausa para simular que la máquina "piensa"
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            int w, z;

            // Buscar posición válida para disparar
            do {
                w = new Random().nextInt(10);
                z = new Random().nextInt(10);
            } while (playerBoard.isWaterHitOrSunk(w, z));

            if (playerBoard.getState(w, z) == Matrix.State.EMPTY) {
                // Disparo al agua - cambiar turno
                game.setTurn();
                playerBoard.changeState(w, z, Matrix.State.WATER);

                Path waterHit = drawWaterHit();
                waterHit.setLayoutX(w * CELL_SIZE);
                waterHit.setLayoutY(z * CELL_SIZE);
                panePosition.getChildren().add(waterHit);

                updateTurnLabel();
                saveGameState();

            } else if (playerBoard.getState(w, z) == Matrix.State.OCCUPIED) {
                // Tocado - la máquina vuelve a disparar
                playerBoard.updateShipStateToHit(w, z);
                playerBoard.updateAndCheckShipStateToSunk();

                updateTurnLabel();
                redrawPlayerBoard();
                saveGameState();

                // Verificar derrota del jugador
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
            serialization.serializeObjects(serialization.getRelativePath(), machineBoard, playerBoard);

            // Guardar estadísticas en archivo plano
            serialization.savePlayerStats(
                    playerBoard.getUsername(),
                    playerBoard.getSunkShips(),
                    machineBoard.getSunkShips()
            );
        } catch (Exception e) {
            System.err.println("Error al guardar el juego: " + e.getMessage());
        }
    }

    /**
     * Updates the turn label to show current player.
     */
    private void updateTurnLabel() {
        if (game.getTurn() % 2 == 0) {
            // Turno del jugador
            labelTurnInGame.setText("⚔ TU TURNO");
            labelTurnInGame.setTextFill(Color.web("#FFD700"));
            informationLabel.setText("¡Haz clic en el tablero enemigo para disparar!");
        } else {
            // Turno de la máquina
            labelTurnInGame.setText("🏴‍☠️ TURNO ENEMIGO");
            labelTurnInGame.setTextFill(Color.web("#FF6B6B"));
            informationLabel.setText("El enemigo está apuntando...");
        }
    }

    /**
     * Handles the end of the game.
     *
     * @param playerWins true if player won, false if machine won
     */
    private void handleGameEnd(boolean playerWins) {
        String title = playerWins ? "¡VICTORIA!" : "DERROTA";
        String message = playerWins ?
                "¡Felicidades " + playerBoard.getUsername() + "! Has hundido toda la flota enemiga." :
                "El enemigo ha hundido toda tu flota. ¡Mejor suerte la próxima vez!";

        new AlertBox().showAlert(title, null, message, Alert.AlertType.INFORMATION);

        // Limpiar archivo de guardado y volver al menú
        try {
            serialization.clearFile(serialization.getRelativePath());
            InformationView infoView = InformationView.getInstance();
            infoView.show();
            ((Stage) nameLabel.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Redraws the player's board to show hit and sunk states.
     */
    private void redrawPlayerBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Matrix.State state = playerBoard.getState(i, j);
                if (state == Matrix.State.HIT || state == Matrix.State.SUNK) {
                    final int fi = i, fj = j;
                    // Remover marcador anterior si existe
                    panePosition.getChildren().removeIf(node ->
                            node.getLayoutX() == fi * CELL_SIZE &&
                                    node.getLayoutY() == fj * CELL_SIZE && node instanceof Path);

                    Path marker = (state == Matrix.State.SUNK) ? drawShipSunk() : drawShipHit();
                    marker.setLayoutX(i * CELL_SIZE);
                    marker.setLayoutY(j * CELL_SIZE);
                    panePosition.getChildren().add(marker);
                }
            }
        }
    }

    /**
     * Redraws the machine's board to show hit and sunk states.
     */
    private void redrawMachineBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Matrix.State state = machineBoard.getState(i, j);
                if (state == Matrix.State.HIT || state == Matrix.State.SUNK) {
                    final int fi = i, fj = j;
                    // Remover marcador anterior si existe
                    panePositionMachine.getChildren().removeIf(node ->
                            node.getLayoutX() == fi * CELL_SIZE &&
                                    node.getLayoutY() == fj * CELL_SIZE && node instanceof Path);

                    Path marker = (state == Matrix.State.SUNK) ? drawShipSunk() : drawShipHit();
                    marker.setLayoutX(i * CELL_SIZE);
                    marker.setLayoutY(j * CELL_SIZE);
                    panePositionMachine.getChildren().add(marker);
                }
            }
        }
    }

    /**
     * Draws the player's board grid with row and column labels.
     */
    public void drawGrid() {
        // Dibujar líneas horizontales y verticales
        for (int i = 0; i <= NUMBERS_CELL; i++) {
            Line hLine = new Line(0, i * CELL_SIZE, GRID_SIZE, i * CELL_SIZE);
            hLine.setStroke(Color.web("#5F9EA0"));
            hLine.setStrokeWidth(0.5);
            panePosition.getChildren().add(hLine);

            Line vLine = new Line(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE);
            vLine.setStroke(Color.web("#5F9EA0"));
            vLine.setStrokeWidth(0.5);
            panePosition.getChildren().add(vLine);
        }

        // Etiquetas de columnas (A-J)
        for (int i = 0; i < NUMBERS_CELL; i++) {
            char letter = (char) (65 + i);
            Label label = new Label(String.valueOf(letter));
            label.setPrefSize(CELL_SIZE, 30);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: #87CEEB; -fx-font-size: 11px; -fx-font-weight: bold;");
            AnchorPane.setLeftAnchor(label, i * (double) CELL_SIZE);
            AnchorPane.setTopAnchor(label, 0.0);
            columnsPane.getChildren().add(label);
        }

        // Etiquetas de filas (1-10) - CORRECCIÓN AQUÍ
        for (int i = 0; i < NUMBERS_CELL; i++) {
            Label label = new Label(String.valueOf(i + 1));
            label.setPrefSize(30, CELL_SIZE);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: #87CEEB; -fx-font-size: 11px; -fx-font-weight: bold;");
            AnchorPane.setRightAnchor(label, 0.0);
            AnchorPane.setTopAnchor(label, i * (double) CELL_SIZE);  // ESTO ALINEA CORRECTAMENTE
            rowsPane.getChildren().add(label);
        }
    }

    /**
     * Draws the machine's board grid with row and column labels.
     */
    public void drawGridMachine() {
        // Dibujar líneas horizontales y verticales
        for (int i = 0; i <= NUMBERS_CELL; i++) {
            Line hLine = new Line(0, i * CELL_SIZE, GRID_SIZE, i * CELL_SIZE);
            hLine.setStroke(Color.web("#8B0000"));
            hLine.setStrokeWidth(0.5);
            panePositionMachine.getChildren().add(hLine);

            Line vLine = new Line(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE);
            vLine.setStroke(Color.web("#8B0000"));
            vLine.setStrokeWidth(0.5);
            panePositionMachine.getChildren().add(vLine);
        }

        // Etiquetas de columnas (A-J)
        for (int i = 0; i < NUMBERS_CELL; i++) {
            char letter = (char) (65 + i);
            Label label = new Label(String.valueOf(letter));
            label.setPrefSize(CELL_SIZE, 30);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px; -fx-font-weight: bold;");
            AnchorPane.setLeftAnchor(label, i * (double) CELL_SIZE);
            AnchorPane.setTopAnchor(label, 0.0);
            columnsPaneMachine.getChildren().add(label);
        }

        // Etiquetas de filas (1-10) - CORRECCIÓN AQUÍ
        for (int i = 0; i < NUMBERS_CELL; i++) {
            Label label = new Label(String.valueOf(i + 1));
            label.setPrefSize(30, CELL_SIZE);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 11px; -fx-font-weight: bold;");
            AnchorPane.setRightAnchor(label, 0.0);
            AnchorPane.setTopAnchor(label, i * (double) CELL_SIZE);  // ESTO ALINEA CORRECTAMENTE
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

            // Escalar barco de 40px a 30px (factor 0.75)
            path.getTransforms().add(new Scale(0.75, 0.75));

            path.setLayoutX(ship.getTailX() * CELL_SIZE);
            path.setLayoutY(ship.getTailY() * CELL_SIZE);
            path.setStrokeWidth(1.5);
            path.setStroke(Color.web("#00d4ff"));
            path.setFill(Color.rgb(0, 212, 255, 0.15));

            // Rotar si el barco está en vertical
            if (ship.getDirection() == Ship.Direction.VERTICAL) {
                Rotate rotate = new Rotate(90, 15, 15);
                path.getTransforms().add(rotate);
            }

            panePosition.getChildren().add(path);
        }
    }

    /**
     * Draws an X shape to indicate a water shot.
     *
     * @return Path with the X figure
     */
    public Path drawWaterHit() {
        Path path = new Path();
        int padding = 6;

        // Línea diagonal \
        path.getElements().add(new MoveTo(padding, padding));
        path.getElements().add(new LineTo(CELL_SIZE - padding, CELL_SIZE - padding));

        // Línea diagonal /
        path.getElements().add(new MoveTo(CELL_SIZE - padding, padding));
        path.getElements().add(new LineTo(padding, CELL_SIZE - padding));

        path.setStroke(Color.web("#4a9eff"));
        path.setStrokeWidth(2.5);
        path.setStrokeLineCap(StrokeLineCap.ROUND);

        return path;
    }

    /**
     * Draws a circle to indicate a hit ship.
     *
     * @return Path with the circle figure
     */
    public Path drawShipHit() {
        Path path = new Path();
        int center = CELL_SIZE / 2;
        int radius = 9;

        // Crear círculo usando arcos
        path.getElements().add(new MoveTo(center + radius, center));
        path.getElements().add(new ArcTo(radius, radius, 0, center - radius, center, false, true));
        path.getElements().add(new ArcTo(radius, radius, 0, center + radius, center, false, true));
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
    public Path drawShipSunk() {
        Path path = new Path();
        int center = CELL_SIZE / 2;
        int radius = 10;
        int pad = 5;

        // Círculo exterior
        path.getElements().add(new MoveTo(center + radius, center));
        path.getElements().add(new ArcTo(radius, radius, 0, center - radius, center, false, true));
        path.getElements().add(new ArcTo(radius, radius, 0, center + radius, center, false, true));

        // X interior
        path.getElements().add(new MoveTo(center - pad, center - pad));
        path.getElements().add(new LineTo(center + pad, center + pad));
        path.getElements().add(new MoveTo(center + pad, center - pad));
        path.getElements().add(new LineTo(center - pad, center + pad));

        path.setStroke(Color.web("#ff4444"));
        path.setStrokeWidth(2);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setFill(Color.web("#ff4444", 0.3));

        return path;
    }

    /**
     * Redraws hit states when loading a saved game.
     */
    public void drawHitsContinue() {
        // Redibujar estados de ambos tableros
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // Tablero del jugador
                Matrix.State state = playerBoard.getState(i, j);
                if (state == Matrix.State.WATER || state == Matrix.State.HIT || state == Matrix.State.SUNK) {
                    Path marker = (state == Matrix.State.WATER) ? drawWaterHit() :
                            (state == Matrix.State.HIT) ? drawShipHit() : drawShipSunk();
                    marker.setLayoutX(i * CELL_SIZE);
                    marker.setLayoutY(j * CELL_SIZE);
                    panePosition.getChildren().add(marker);
                }

                // Tablero de la máquina
                Matrix.State machineState = machineBoard.getState(i, j);
                if (machineState == Matrix.State.WATER || machineState == Matrix.State.HIT || machineState == Matrix.State.SUNK) {
                    Path marker = (machineState == Matrix.State.WATER) ? drawWaterHit() :
                            (machineState == Matrix.State.HIT) ? drawShipHit() : drawShipSunk();
                    marker.setLayoutX(i * CELL_SIZE);
                    marker.setLayoutY(j * CELL_SIZE);
                    panePositionMachine.getChildren().add(marker);
                }
            }
        }
    }
}