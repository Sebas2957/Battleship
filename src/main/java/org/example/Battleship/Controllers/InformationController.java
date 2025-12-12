package org.example.Battleship.Controllers;

import org.example.Battleship.Models.Matrix;
import org.example.Battleship.Models.utilities.serialization;
import org.example.Battleship.Views.GameView;
import org.example.Battleship.Views.PlacementView;
import org.example.Battleship.Views.AlertBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller for the information view (main menu) of Battleship.
 * Handles game start logic, saved game loading and instructions display.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class InformationController {

    private serialization serialization;
    private Matrix machineBoard;
    private Matrix playerBoard;

    @FXML
    private Label welcomeText;

    /**
     * Initializes the controller.
     * Runs automatically after loading the FXML.
     */
    @FXML
    public void initialize() {
        serialization = new serialization();
        checkAndLoadGame();
    }

    /**
     * Handles the instructions button event.
     * Shows an alert with the game rules.
     *
     * @param event Button action event
     */
    @FXML
    void onInformationButton(ActionEvent event) {
        new AlertBox().showAlert(
                "Instrucciones",
                "Bienvenido al juego Battle Ship",
                "El juego Battle Ship es un juego de estrategia donde dos jugadores " +
                        "(humano y máquina) compiten por hundir la flota del oponente.\n\n" +
                        "• Agua (X): Pasa el turno al oponente\n" +
                        "• Tocado: Vuelve a disparar\n" +
                        "• Hundido: Vuelve a disparar\n\n" +
                        "¡Hunde toda la flota enemiga para ganar!",
                Alert.AlertType.INFORMATION
        );
    }

    /**
     * Checks if a saved game exists and asks user if they want to load it.
     */
    public void checkAndLoadGame() {
        String relativePath = serialization.getRelativePath();
        File saveFile = new File(relativePath);

        // Verificar si existe un archivo de guardado válido
        if (saveFile.exists() && saveFile.length() > 0) {
            try {
                // Verificar si el juego guardado ya terminó
                List<Object> objects = serialization.deserializeObjects(relativePath);
                if (objects.size() >= 2) {
                    Matrix tempMachine = (Matrix) objects.get(0);
                    Matrix tempPlayer = (Matrix) objects.get(1);

                    // Si el juego terminó, iniciar uno nuevo
                    if (tempMachine.allShipsSunk() || tempPlayer.allShipsSunk()) {
                        System.out.println("El juego anterior ya terminó. Se iniciará uno nuevo.");
                        serialization.clearFile(relativePath);
                        return;
                    }
                }

                // Preguntar al usuario si desea continuar
                AlertBox alertBox = new AlertBox();
                boolean confirmed = alertBox.showConfirmation(
                        "Partida Guardada",
                        "Se encontró una partida guardada. ¿Deseas continuar con el juego anterior?\n\n" +
                                "Si seleccionas 'No', se iniciará un juego nuevo."
                );

                if (confirmed) {
                    this.loadGame();
                } else {
                    // Usuario decidió iniciar un juego nuevo
                    serialization.clearFile(relativePath);
                }
            } catch (Exception e) {
                System.err.println("Error al verificar partida guardada: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the play button event.
     * Starts a new game from the ship placement screen.
     *
     * @param event Button action event
     */
    @FXML
    void onPlayButton(ActionEvent event) {
        AlertBox alertBox = new AlertBox();
        boolean confirmed = alertBox.showConfirmation(
                "Confirmación",
                "¿Estás seguro que quieres comenzar a jugar?"
        );

        if (confirmed) {
            try {
                PlacementView placementView = new PlacementView();
                placementView.show();
                ((Stage) welcomeText.getScene().getWindow()).close();
            } catch (IOException e) {
                System.err.println("Error al abrir la vista de colocación: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads a previously saved game.
     */
    public void loadGame() {
        try {
            List<Object> objects = serialization.deserializeObjects(serialization.getRelativePath());

            if (objects.size() >= 2) {
                machineBoard = (Matrix) objects.get(0);
                playerBoard = (Matrix) objects.get(1);

                System.out.println("Partida cargada exitosamente.");
                System.out.println("Jugador: " + playerBoard.getUsername());
                System.out.println("Barcos hundidos del jugador: " + playerBoard.getSunkShips());
                System.out.println("Barcos hundidos de la máquina: " + machineBoard.getSunkShips());

                // Abrir la vista de juego con los tableros cargados
                GameView gameView = new GameView(machineBoard, playerBoard);
                gameView.show();

                Stage stage = (Stage) welcomeText.getScene().getWindow();
                stage.close();
            } else {
                throw new IllegalStateException("Archivo de guardado corrupto");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar la partida: " + e.getMessage());
            e.printStackTrace();

            new AlertBox().showAlert(
                    "Error",
                    "No se pudo cargar la partida",
                    "El archivo de guardado está corrupto o no es válido.",
                    Alert.AlertType.ERROR
            );
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}