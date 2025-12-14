package org.example.Battleship.Models.interfaces;

/**
 * Interface for the Observer design pattern.
 * Classes that want to receive game notifications must implement this.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public interface GameObserver {

    /**
     * Called when a game event occurs.
     *
     * @param event Event type: "WATER", "HIT", "SUNK", "GAME_END"
     * @param message Event description
     */
    void update(String event, String message);
}