package org.example.Battleship.Models;

import org.example.Battleship.Models.factories.ShipFactory;
import org.example.Battleship.Models.structures.ShotQueue;

import java.io.Serializable;
import java.util.*;

/**
 * Represents the 10x10 game board matrix for Battleship.
 * Manages cell states, ship positions and shot logic.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class Matrix implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Possible states for a cell on the board.
     */
    public enum State {
        EMPTY,
        OCCUPIED,
        WATER,
        HIT,
        SUNK
    }

    private final ArrayList<ArrayList<State>> board = new ArrayList<>();
    private final List<Ship> ships = new ArrayList<>();
    private final ShotQueue shotHistory; // Estructura de datos: Cola
    private final int BOARD_SIZE = 10;
    private String username;
    private int sunkShips;

    /**
     * Constructor: creates board and places ships randomly.
     */
    public Matrix() {
        // Crear cola para historial de disparos
        shotHistory = new ShotQueue();

        // Inicializar tablero vacío
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.add(new ArrayList<>());
            for (int j = 0; j < BOARD_SIZE; j++) {
                board.get(i).add(State.EMPTY);
            }
        }

        // Usar Factory para crear la flota (Patrón Factory)
        ships.addAll(ShipFactory.createFleet());

        // Colocar barcos aleatoriamente
        Random random = new Random();
        for (Ship ship : ships) {
            int x, y;
            do {
                x = random.nextInt(BOARD_SIZE);
                y = random.nextInt(BOARD_SIZE);
            } while (!this.putShip(x, y, ship));

            ship.setPosition(x, y);
        }

        sunkShips = 0;
    }

    /**
     * Places a ship on the board.
     *
     * @param x Column (0-9)
     * @param y Row (0-9)
     * @param ship Ship to place
     * @return true if placed successfully
     */
    public boolean putShip(int x, int y, Ship ship) {
        if (validatePosition(x, y, ship)) {
            Ship.Direction direction = ship.getDirection();
            int length = ship.getLength();

            for (int i = 0; i < length; i++) {
                int r = direction == Ship.Direction.VERTICAL ? y + i : y;
                int c = direction == Ship.Direction.HORIZONTAL ? x + i : x;
                board.get(r).set(c, State.OCCUPIED);
            }
            return true;
        }
        return false;
    }

    /**
     * Validates if a ship can be placed at a position.
     *
     * @param x Column
     * @param y Row
     * @param ship Ship to validate
     * @return true if position is valid
     */
    public boolean validatePosition(int x, int y, Ship ship) {
        Ship.Direction direction = ship.getDirection();
        int length = ship.getLength();

        for (int i = 0; i < length; i++) {
            int r = direction == Ship.Direction.VERTICAL ? y + i : y;
            int c = direction == Ship.Direction.HORIZONTAL ? x + i : x;

            // Verificar límites del tablero
            if (c >= BOARD_SIZE || r >= BOARD_SIZE || c < 0 || r < 0) {
                return false;
            }

            // Verificar que la casilla esté vacía
            if (board.get(r).get(c) != State.EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes a ship from the board.
     *
     * @param x Column
     * @param y Row
     * @param ship Ship to remove
     */
    public void removeShip(int x, int y, Ship ship) {
        Ship.Direction direction = ship.getDirection();
        int length = ship.getLength();

        for (int i = 0; i < length; i++) {
            int r = direction == Ship.Direction.VERTICAL ? y + i : y;
            int c = direction == Ship.Direction.HORIZONTAL ? x + i : x;
            board.get(r).set(c, State.EMPTY);
        }
    }

    /**
     * Gets the state of a cell.
     *
     * @param x Column
     * @param y Row
     * @return Cell state
     */
    public State getState(int x, int y) {
        return board.get(y).get(x);
    }

    /**
     * Changes the state of a cell.
     *
     * @param x Column
     * @param y Row
     * @param newState New state
     */
    public void changeState(int x, int y, State newState) {
        board.get(y).set(x, newState);
    }

    /**
     * Checks if a cell has already been shot.
     *
     * @param x Column
     * @param y Row
     * @return true if already shot
     */
    public boolean isWaterHitOrSunk(int x, int y) {
        State state = board.get(y).get(x);
        return state == State.WATER || state == State.HIT || state == State.SUNK;
    }

    /**
     * Checks if a cell has a hit or sunk ship.
     *
     * @param x Column
     * @param y Row
     * @return true if hit or sunk
     */
    public boolean isHitOrSunk(int x, int y) {
        State state = board.get(y).get(x);
        return state == State.HIT || state == State.SUNK;
    }

    /**
     * Updates ship state to HIT when it receives a shot.
     *
     * @param x Column of the shot
     * @param y Row of the shot
     */
    public void updateShipStateToHit(int x, int y) {
        for (Ship ship : ships) {
            int shipX = ship.getTailX();
            int shipY = ship.getTailY();
            int length = ship.getLength();
            Ship.Direction direction = ship.getDirection();

            for (int i = 0; i < length; i++) {
                int currentX = direction == Ship.Direction.HORIZONTAL ? shipX + i : shipX;
                int currentY = direction == Ship.Direction.VERTICAL ? shipY + i : shipY;

                if (currentX == x && currentY == y) {
                    changeState(x, y, State.HIT);
                    ship.incrementHits();
                    return;
                }
            }
        }
    }

    /**
     * Checks and updates ships that have been completely sunk.
     */
    public void updateAndCheckShipStateToSunk() {
        for (Ship ship : ships) {
            int shipX = ship.getTailX();
            int shipY = ship.getTailY();
            int length = ship.getLength();
            Ship.Direction direction = ship.getDirection();
            boolean allHit = true;

            // Verificar si todas las partes fueron golpeadas
            for (int i = 0; i < length; i++) {
                int currentX = direction == Ship.Direction.HORIZONTAL ? shipX + i : shipX;
                int currentY = direction == Ship.Direction.VERTICAL ? shipY + i : shipY;

                if (board.get(currentY).get(currentX) != State.HIT) {
                    allHit = false;
                    break;
                }
            }

            // Si todo fue golpeado, marcar como hundido
            if (allHit) {
                for (int i = 0; i < length; i++) {
                    int sunkX = direction == Ship.Direction.HORIZONTAL ? shipX + i : shipX;
                    int sunkY = direction == Ship.Direction.VERTICAL ? shipY + i : shipY;
                    changeState(sunkX, sunkY, State.SUNK);
                }
                setSunkShips();
            }
        }
    }

    /**
     * Checks if all ships have been sunk.
     *
     * @return true if all are sunk
     */
    public boolean allShipsSunk() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.get(i).get(j) == State.OCCUPIED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Records a shot in the history queue.
     *
     * @param x Column
     * @param y Row
     * @param isPlayer true if player shot
     * @param result "WATER", "HIT" or "SUNK"
     */
    public void recordShot(int x, int y, boolean isPlayer, String result) {
        shotHistory.addShot(x, y, isPlayer, result);
    }

    /**
     * Gets the shot history queue.
     *
     * @return Queue with shot history
     */
    public ShotQueue getShotHistory() {
        return shotHistory;
    }

    // ==================== GETTERS Y SETTERS ====================

    public Ship getShip(int i) {
        return ships.get(i);
    }

    public List<Ship> getShips() {
        return ships;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSunkShips() {
        return sunkShips;
    }

    public void setSunkShips() {
        this.sunkShips += 1;
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    /**
     * Prints board to console (for debugging).
     */
    public void getBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
}