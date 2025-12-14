package org.example.Battleship.Models.exceptions;

/**
 * Exception thrown when a board position is invalid.
 * For example: when a ship goes outside the board or overlaps another.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class InvalidPositionException extends Exception {

    /**
     * Creates a new exception with a message.
     *
     * @param message Error description
     */
    public InvalidPositionException(String message) {
        super(message);
    }
}