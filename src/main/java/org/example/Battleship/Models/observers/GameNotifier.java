package org.example.Battleship.Models.observers;

import org.example.Battleship.Models.interfaces.GameObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that notifies game events to observers (Observer Pattern).
 * Also uses Singleton pattern to have a single instance.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class GameNotifier {

    // Instancia única (Singleton)
    private static GameNotifier instance;

    // Lista de observadores
    private List<GameObserver> observers;

    /**
     * Private constructor (Singleton).
     */
    private GameNotifier() {
        observers = new ArrayList<>();
    }

    /**
     * Gets the singleton instance.
     *
     * @return The GameNotifier instance
     */
    public static GameNotifier getInstance() {
        if (instance == null) {
            instance = new GameNotifier();
        }
        return instance;
    }

    /**
     * Adds an observer to receive notifications.
     *
     * @param observer The observer to add
     */
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Removes an observer.
     *
     * @param observer The observer to remove
     */
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers of an event.
     *
     * @param event Event type
     * @param message Descriptive message
     */
    public void notifyObservers(String event, String message) {
        // Notificar a todos los observadores registrados
        for (GameObserver observer : observers) {
            observer.update(event, message);
        }
    }

    /**
     * Clears all observers (useful when restarting game).
     */
    public void clear() {
        observers.clear();
    }
}