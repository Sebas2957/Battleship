package org.example.Battleship.Controllers;

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

    private final int GRID_SIZE = 400;
    private final int NUMBERS_CELL = 10;
    private final int CELL_SIZE = GRID_SIZE / NUMBERS_CELL;

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

            positionInitialX = (int) (this.targetPath.getLayoutX() / CELL_SIZE);
            positionInitialY = (int) (this.targetPath.getLayoutY() / CELL_SIZE);

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
            int positionX = (int) event.getX();
            int positionY = (int) event.getY();

            isDragging = true;

            int positionFixX = (positionX / CELL_SIZE) * CELL_SIZE;
            int positionFixY = (positionY / CELL_SIZE) * CELL_SIZE;

            movementValid = playerBoard.validatePosition(positionX / CELL_SIZE, positionY / CELL_SIZE, targetShip);

            if (movementValid) {
                this.targetPath.setLayoutX(positionFixX);
                this.targetPath.setLayoutY(positionFixY);
                this.targetPath.setStroke(Color.web("#40bf44"));
                this.targetPath.setFill(Color.rgb(64, 191, 68, 0.05));
            } else {
                this.targetPath.setLayoutX(event.getX());
                this.targetPath.setLayoutY(event.getY());
                this.targetPath.setStroke(Color.web("#00f"));
                this.targetPath.setFill(Color.rgb(0, 0, 255, 0.05));
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
            this.targetPath.setStroke(Color.web("#00f"));
            this.targetPath.setFill(Color.rgb(0, 0, 255, 0.05));

            if (isDragging) {
                int positionX = (int) (this.targetPath.getLayoutX() / CELL_SIZE);
                int positionY = (int) (this.targetPath.getLayoutY() / CELL_SIZE);

                if (movementValid) {
                    this.targetShip.setTailX(positionX);
                    this.targetShip.setTailY(positionY);
                } else {
                    this.targetPath.setLayoutX(positionInitialX * CELL_SIZE);
                    this.targetPath.setLayoutY(positionInitialY * CELL_SIZE);
                }
            } else {
                // Rotar el barco al hacer clic
                playerBoard.removeShip(this.targetShip.getTailX(), this.targetShip.getTailY(), this.targetShip);
                this.targetShip.rotate();

                movementValid = playerBoard.validatePosition(
                        this.targetShip.getTailX(),
                        this.targetShip.getTailY(),
                        this.targetShip
                );

                if (movementValid) {
                    if (targetShip.getDirection() == Ship.Direction.VERTICAL) {
                        Rotate rotate = new Rotate(90, 20, 20);
                        targetPath.getTransforms().add(rotate);
                    } else {
                        Rotate rotate = new Rotate(-90, 20, 20);
                        targetPath.getTransforms().add(rotate);
                    }
                } else {
                    this.targetShip.rotate();
                }
            }

            playerBoard.putShip(this.targetShip.getTailX(), this.targetShip.getTailY(), this.targetShip);

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
            label.setStyle("-fx-font-size: 18px;");

            AnchorPane.setLeftAnchor(label, i * 40.0);
            AnchorPane.setTopAnchor(label, 0.0);
            columnsPane.getChildren().add(label);

            // Etiquetas de filas (1-10)
            label = new Label(String.valueOf(i + 1));
            label.setPrefSize(40, 40);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 18px;");

            AnchorPane.setLeftAnchor(label, 0.0);
            AnchorPane.setTopAnchor(label, i * 40.0);
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
            path.setStroke(Color.web("#00f"));
            path.setFill(Color.rgb(0, 0, 255, 0.05));

            if (ship.getDirection() == Ship.Direction.VERTICAL) {
                Rotate rotate = new Rotate(90, 20, 20);
                path.getTransforms().add(rotate);
            }

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
                "¿Estás seguro que quieres ver ambos tableros?"
        );

        if (confirmed) {
            try {
                playerBoard.setUsername(textFieldName.getText());
                MachineView machineView = new MachineView(machineBoard, playerBoard);
                machineView.show();
                ((Stage) panePosition.getScene().getWindow()).close();
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