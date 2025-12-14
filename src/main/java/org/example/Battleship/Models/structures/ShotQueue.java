package org.example.Battleship.Models.structures;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Queue data structure to store the game's shot history.
 * Uses FIFO principle: First In, First Out.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class ShotQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    // Cola que guarda los disparos como texto
    private Queue<String> shots;

    /**
     * Constructor: creates an empty queue.
     */
    public ShotQueue() {
        shots = new LinkedList<>();
    }

    /**
     * Adds a shot to the queue.
     *
     * @param x Column of the shot (0-9)
     * @param y Row of the shot (0-9)
     * @param isPlayer true if player shot, false if machine
     * @param result "WATER", "HIT" or "SUNK"
     */
    public void addShot(int x, int y, boolean isPlayer, String result) {
        // Convertir columna a letra (0=A, 1=B, etc.)
        char column = (char) ('A' + x);
        int row = y + 1;

        String who = isPlayer ? "Player" : "Machine";
        String shot = who + " shot at " + column + row + ": " + result;

        // offer() agrega al final de la cola
        shots.offer(shot);
    }

    /**
     * Gets and removes the oldest shot.
     *
     * @return The oldest shot, or null if queue is empty
     */
    public String getShot() {
        // poll() remueve y retorna el primero
        return shots.poll();
    }

    /**
     * Peeks at the oldest shot without removing it.
     *
     * @return The oldest shot, or null if queue is empty
     */
    public String peekShot() {
        // peek() solo mira, no remueve
        return shots.peek();
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if there are no shots
     */
    public boolean isEmpty() {
        return shots.isEmpty();
    }

    /**
     * Gets the number of shots in the queue.
     *
     * @return Number of shots
     */
    public int size() {
        return shots.size();
    }

    /**
     * Clears all shots from the queue.
     */
    public void clear() {
        shots.clear();
    }

    /**
     * Returns all shots as a formatted string.
     *
     * @return Shot history
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SHOT HISTORY ===\n");

        int number = 1;
        for (String shot : shots) {
            sb.append(number).append(". ").append(shot).append("\n");
            number++;
        }

        return sb.toString();
    }
}