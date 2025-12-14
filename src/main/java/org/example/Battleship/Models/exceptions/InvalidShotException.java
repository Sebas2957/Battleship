package org.example.Battleship.Models.exceptions;

/**
 * Exception thrown when a shot is invalid.
 * For example: when shooting twice at the same cell.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class InvalidShotException extends Exception {

    /**
     * Creates a new exception with a message.
     *
     * @param message Error description
     */
    public InvalidShotException(String message) {
        super(message);
    }
}