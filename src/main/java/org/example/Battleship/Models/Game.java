package org.example.Battleship.Models;

import java.io.Serializable;

/**
 * Represents the game state for Battleship.
 * Manages the turn counter to alternate between player and machine.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @version 1.0
 */
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    // Turno par = Jugador, Turno impar = Máquina
    private int turn;

    /**
     * Constructor that initializes the game.
     * Game starts at turn 0 (player's turn).
     */
    public Game() {
        this.turn = 0;
    }

    /**
     * Increments the turn counter.
     */
    public void setTurn() {
        this.turn += 1;
    }

    /**
     * Gets the current turn number.
     *
     * @return Current turn number
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Sets the turn number directly.
     * Useful when loading a saved game.
     *
     * @param turn New turn number
     */
    public void setTurnValue(int turn) {
        this.turn = turn;
    }

    /**
     * Checks if it's the player's turn.
     *
     * @return true if player's turn (even turn)
     */
    public boolean isPlayerTurn() {
        return turn % 2 == 0;
    }

    /**
     * Checks if it's the machine's turn.
     *
     * @return true if machine's turn (odd turn)
     */
    public boolean isMachineTurn() {
        return turn % 2 != 0;
    }

    /**
     * Resets the game to initial turn.
     */
    public void reset() {
        this.turn = 0;
    }

    @Override
    public String toString() {
        return "Game{" +
                "turn=" + turn +
                ", isPlayerTurn=" + isPlayerTurn() +
                '}';
    }
}