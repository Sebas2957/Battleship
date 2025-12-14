package org.example.Battleship.Models.utilities;

import org.example.Battleship.Models.Matrix;
import java.io.Serializable;

/**
 * Represents the complete state of a Battleship game.
 * Allows saving and loading game progress.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private Matrix machineBoard;
    private Matrix playerBoard;
    private int currentTurn;
    private boolean gameFinished;

    /**
     * Constructor with all parameters.
     *
     * @param machineBoard Machine's board
     * @param playerBoard Player's board
     * @param currentTurn Current turn
     * @param gameFinished Game state
     */
    public GameState(Matrix machineBoard, Matrix playerBoard, int currentTurn, boolean gameFinished) {
        this.machineBoard = machineBoard;
        this.playerBoard = playerBoard;
        this.currentTurn = currentTurn;
        this.gameFinished = gameFinished;
    }

    /**
     * Simplified constructor.
     *
     * @param machineBoard Machine's board
     * @param playerBoard Player's board
     */
    public GameState(Matrix machineBoard, Matrix playerBoard) {
        this(machineBoard, playerBoard, 0, false);
    }

    /**
     * Gets the machine's board.
     *
     * @return Machine's board
     */
    public Matrix getMachineBoard() {
        return machineBoard;
    }

    /**
     * Gets the player's board.
     *
     * @return Player's board
     */
    public Matrix getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Gets the current turn.
     *
     * @return Current turn number
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Checks if game has finished.
     *
     * @return true if game finished, false otherwise
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /**
     * Sets the machine's board.
     *
     * @param machineBoard New machine's board
     */
    public void setMachineBoard(Matrix machineBoard) {
        this.machineBoard = machineBoard;
    }

    /**
     * Sets the player's board.
     *
     * @param playerBoard New player's board
     */
    public void setPlayerBoard(Matrix playerBoard) {
        this.playerBoard = playerBoard;
    }

    /**
     * Sets the current turn.
     *
     * @param currentTurn New turn
     */
    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    /**
     * Sets if game has finished.
     *
     * @param gameFinished Game state
     */
    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    /**
     * Checks if either player has lost all ships.
     *
     * @return true if game should end
     */
    public boolean shouldEndGame() {
        return machineBoard.allShipsSunk() || playerBoard.allShipsSunk();
    }

    /**
     * Determines who won the game.
     *
     * @return "Player" if player won, "Machine" if machine won, null if not finished
     */
    public String getWinner() {
        if (machineBoard.allShipsSunk()) {
            return "Player";
        } else if (playerBoard.allShipsSunk()) {
            return "Machine";
        }
        return null;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "currentTurn=" + currentTurn +
                ", gameFinished=" + gameFinished +
                ", playerName=" + (playerBoard != null ? playerBoard.getUsername() : "Unknown") +
                ", playerSunkShips=" + (playerBoard != null ? playerBoard.getSunkShips() : 0) +
                ", machineSunkShips=" + (machineBoard != null ? machineBoard.getSunkShips() : 0) +
                '}';
    }
}