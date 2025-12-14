package org.example.Battleship.Controllers;

import javafx.scene.Node;
import org.example.Battleship.Models.Matrix;
import org.example.Battleship.Models.Ship;
import org.example.Battleship.Views.GameView;
import org.example.Battleship.Views.MachineView;
import org.example.Battleship.Views.AlertBox;
import org.example.Battleship.Models.utilities.serialization;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.geometry.Pos;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

/**
 * Controller for the ship placement view.
 * Allows player to drag and rotate ships on the board before starting.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class PlacementController {

    private static PlacementController instance;

    @FXML private AnchorPane columnsPane;
    @FXML private AnchorPane panePosition;
    @FXML private AnchorPane rowsPane;
    @FXML private TextField textFieldName;

    // Tamaño del tablero en píxeles (400x400)
    private final int GRID_SIZE = 400;
    private final int NUMBERS_CELL = 10;
    private final int CELL_SIZE = GRID_SIZE / NUMBERS_CELL; // 40px por celda

    private Matrix playerBoard;
    private Matrix machineBoard;

    // Variables para drag and drop
    private boolean movementValid;
    private int positionInitialX;
    private int positionInitialY;
    private boolean isDragging;
    private Path targetPath;
    private Ship targetShip;

    /**
     * Gets the singleton instance of the controller.
     *
     * @return PlacementController instance
     */
    public static PlacementController getInstance() {
        return instance;
    }

    /**
     * Gets the player's board.
     *
     * @return Player's Matrix board
     */
    public Matrix getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Gets the machine's board.
     *
     * @return Machine's Matrix board
     */
    public Matrix getMachineBoard() {
        return machineBoard;
    }

    /**
     * Shows the placement window.
     */
    public void show() {
        Stage stage = (Stage) panePosition.getScene().getWindow();
        stage.show();
    }

    /**
     * Initializes the controller.
     * Configures player and machine boards.
     */
    public void initialize() {
        instance = this;
        playerBoard = new Matrix();
        machineBoard = new Matrix();
        movementValid = false;

        this.drawGrid();
        this.drawShips();

        // Arregla el error de estiramiento de recuadro
        panePosition.setPrefSize(GRID_SIZE, GRID_SIZE);
        panePosition.setMaxSize(GRID_SIZE, GRID_SIZE);
        panePosition.setMinSize(GRID_SIZE, GRID_SIZE);

        // Configurar eventos de mouse para drag and drop
        panePosition.setOnMouseMoved(this::handleMouseMoved);
        panePosition.setOnMousePressed(this::handleMousePressed);
        panePosition.setOnMouseDragged(this::handleMouseDragged);
        panePosition.setOnMouseReleased(this::handleMouseReleased);
    }

    /**
     * Handles mouse movement over the board.
     * Changes cursor when over a ship.
     *
     * @param event Mouse event
     */
    private void handleMouseMoved(MouseEvent event) {
        try {
            this.targetPath = (Path) event.getTarget();
            this.panePosition.setCursor(Cursor.MOVE);
        } catch (Exception e) {
            panePosition.setCursor(Cursor.DEFAULT);
        }
    }

    /**
     * Handles mouse press event.
     * Starts the ship dragging process.
     *
     * @param event Mouse event
     */
    public void handleMousePressed(MouseEvent event) {
        try {
            this.targetPath = (Path) event.getTarget();
            this.targetShip = (Ship) targetPath.getUserData();

            // Guardar posición inicial del barco
            positionInitialX = (int) (this.targetPath.getLayoutX() / CELL_SIZE);
            positionInitialY = (int) (this.targetPath.getLayoutY() / CELL_SIZE);

            // Remover barco del tablero mientras se arrastra
            playerBoard.removeShip(positionInitialX, positionInitialY, this.targetShip);
            this.targetPath.setStroke(Color.GREEN);
        } catch (Exception ignored) {}
    }

    /**
     * Handles mouse drag event.
     * Moves the ship and validates position in real time.
     *
     * @param event Mouse event
     */
    public void handleMouseDragged(MouseEvent event) {
        try {
            isDragging = true;

            // Obtener posición del mouse
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Calcular la celda objetivo
            int cellX = (int) (mouseX / CELL_SIZE);
            int cellY = (int) (mouseY / CELL_SIZE);

            // Obtener dimensiones del barco en celdas
            int shipWidthCells = targetShip.getWidth();
            int shipHeightCells = targetShip.getHeight();

            // Limitar las celdas para que el barco no se salga del tablero
            int maxCellX = NUMBERS_CELL - shipWidthCells;
            int maxCellY = NUMBERS_CELL - shipHeightCells;

            // Clampear la celda dentro de los límites válidos
            cellX = Math.max(0, Math.min(cellX, maxCellX));
            cellY = Math.max(0, Math.min(cellY, maxCellY));

            // Calcular la posición en píxeles (ajustada a la cuadrícula)
            double newLayoutX = cellX * CELL_SIZE;
            double newLayoutY = cellY * CELL_SIZE;

            // Validar si la posición es válida (no hay otros barcos)
            movementValid = playerBoard.validatePosition(cellX, cellY, targetShip);

            // Aplicar posición
            this.targetPath.setLayoutX(newLayoutX);
            this.targetPath.setLayoutY(newLayoutY);

            // Colorear según validez
            if (movementValid) {
                this.targetPath.setStroke(Color.web("#40bf44"));
                this.targetPath.setFill(Color.rgb(64, 191, 68, 0.05));
            } else {
                this.targetPath.setStroke(Color.web("#f00"));
                this.targetPath.setFill(Color.rgb(255, 0, 0, 0.05));
            }
        } catch (Exception ignored) {}
    }

    /**
     * Handles mouse release event.
     * Completes ship movement or rotation.
     *
     * @param event Mouse event
     */
    public void handleMouseReleased(MouseEvent event) {
        try {
            // Color final del barco (Rojo)
            this.targetPath.setStroke(Color.web("#f00"));
            this.targetPath.setFill(Color.rgb(255, 0, 0, 0.05));

            if (isDragging) {
                // Se estaba arrastrando el barco
                int positionX = (int) (this.targetPath.getLayoutX() / CELL_SIZE);
                int positionY = (int) (this.targetPath.getLayoutY() / CELL_SIZE);

                if (movementValid) {
                    // Actualizar posición del barco
                    this.targetShip.setTailX(positionX);
                    this.targetShip.setTailY(positionY);
                } else {
                    // Volver a la posición inicial si no es válida
                    this.targetPath.setLayoutX(positionInitialX * CELL_SIZE);
                    this.targetPath.setLayoutY(positionInitialY * CELL_SIZE);
                }
            } else {
                // Se hizo clic sin arrastrar - rotar el barco
                playerBoard.removeShip(this.targetShip.getTailX(), this.targetShip.getTailY(), this.targetShip);
                this.targetShip.rotate();

                // Validar si la rotación es válida
                movementValid = playerBoard.validatePosition(
                        this.targetShip.getTailX(),
                        this.targetShip.getTailY(),
                        this.targetShip
                );

                if (movementValid) {
                    // Aplicar rotación visual
                    if (targetShip.getDirection() == Ship.Direction.VERTICAL) {
                        Rotate rotate = new Rotate(90, 20, 20);
                        targetPath.getTransforms().add(rotate);
                    } else {
                        Rotate rotate = new Rotate(-90, 20, 20);
                        targetPath.getTransforms().add(rotate);
                    }
                } else {
                    // Si la rotación no es válida, revertirla
                    this.targetShip.rotate();
                }
            }

            // Colocar el barco en su posición final
            playerBoard.putShip(this.targetShip.getTailX(), this.targetShip.getTailY(), this.targetShip);

            // Limpiar referencias
            this.targetPath = null;
            this.targetShip = null;
            this.isDragging = false;
        } catch (Exception ignored) {}
    }

    /**
     * Draws the board grid with row and column labels.
     */
    public void drawGrid() {
        Line line;

        // Dibujar líneas de la cuadrícula
        for (int i = 0; i <= NUMBERS_CELL; i++) {
            line = new Line(0, i * CELL_SIZE, GRID_SIZE, i * CELL_SIZE);
            line.setStroke(Color.web("#b4b4ff"));
            line.setStrokeWidth(1);
            panePosition.getChildren().add(line);

            line = new Line(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE);
            line.setStrokeWidth(1);
            line.setStroke(Color.web("#b4b4ff"));
            panePosition.getChildren().add(line);
        }

        Label label;

        // Etiquetas de columnas (A-J)
        for (int i = 0; i < NUMBERS_CELL; i++) {
            char letter = (char) (65 + i);

            label = new Label(String.valueOf(letter));
            label.setPrefSize(40, 40);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

            AnchorPane.setLeftAnchor((Node) label, (double) (i * CELL_SIZE));
            AnchorPane.setTopAnchor(label, 0.0);
            columnsPane.getChildren().add(label);

            // Etiquetas de filas (1-10)
            label = new Label(String.valueOf(i + 1));
            label.setPrefSize(40, 40);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

            AnchorPane.setRightAnchor(label, 0.0);
            AnchorPane.setTopAnchor((Node) label, (double) (i * CELL_SIZE));
            rowsPane.getChildren().add(label);
        }
    }

    /**
     * Draws ships in their initial positions.
     */
    public void drawShips() {
        Ship ship;
        Path path;

        for (int i = 0; i < 10; i++) {
            ship = playerBoard.getShip(i);
            path = ship.getDraw();

            path.setLayoutX(ship.getTailX() * CELL_SIZE);
            path.setLayoutY(ship.getTailY() * CELL_SIZE);
            path.setStrokeWidth(2);
            path.setStroke(Color.web("#f00"));
            path.setFill(Color.rgb(255, 0, 0, 0.05));

            // Rotar si el barco está en vertical
            if (ship.getDirection() == Ship.Direction.VERTICAL) {
                Rotate rotate = new Rotate(90, 20, 20);
                path.getTransforms().add(rotate);
            }

            // Guardar referencia del barco en el Path
            path.setUserData(ship);
            panePosition.getChildren().add(path);
        }
    }

    /**
     * Handles the "View machine board" button event.
     *
     * @param event Action event
     */
    @FXML
    void onActionMachineView(ActionEvent event) {
        if (!validateName()) return;

        AlertBox alertBox = new AlertBox();
        boolean confirmed = alertBox.showConfirmation(
                "Confirmación",
                "¿Estás seguro que quieres ver ambos tableros?\n" +
                        "(Solo para verificación)"
        );

        if (confirmed) {
            try {
                playerBoard.setUsername(textFieldName.getText());
                // Mostrar MachineView sin cerrar PlacementView
                MachineView machineView = new MachineView(machineBoard, playerBoard);
                machineView.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the "Play" button event.
     * Starts the game after validating player name.
     *
     * @param event Action event
     */
    @FXML
    void onActionPlayButton(ActionEvent event) {
        if (!validateName()) return;

        AlertBox alertBox = new AlertBox();
        boolean confirmed = alertBox.showConfirmation(
                "Confirmación",
                "¿Estás seguro que quieres comenzar una partida?\n" +
                        "(Los barcos no se podrán mover una vez iniciada la partida)"
        );

        if (confirmed) {
            try {
                playerBoard.setUsername(textFieldName.getText());

                // Guardar el estado inicial del juego
                serialization.serializeObjects("objectsSerialization.txt", machineBoard, playerBoard);
                serialization.savePlayerStats(playerBoard.getUsername(), 0, 0);

                // Abrir vista de juego y cerrar esta ventana
                GameView gameView = new GameView(machineBoard, playerBoard);
                gameView.show();
                ((Stage) panePosition.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Validates that player has entered a name.
     *
     * @return true if name is valid, false otherwise
     */
    private boolean validateName() {
        boolean nameEmpty = textFieldName.getText().isEmpty();

        if (nameEmpty) {
            new AlertBox().showAlert(
                    "Error",
                    "No puedes continuar",
                    "Debes digitar un nombre para poder iniciar.",
                    Alert.AlertType.ERROR
            );
            return false;
        }

        return true;
    }
}