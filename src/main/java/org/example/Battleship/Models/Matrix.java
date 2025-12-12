package org.example.Battleship.Models;

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
    private final int BOARD_SIZE = 10;
    private String username;
    private int sunkShips;

    /**
     * Constructor that initializes empty board and places ships randomly.
     */
    public Matrix() {
        // Inicializar tablero vacío
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.add(new ArrayList<>());
            for (int j = 0; j < BOARD_SIZE; j++) {
                board.get(i).add(State.EMPTY);
            }
        }

        // Inicializar flota: 1 portaaviones, 2 submarinos, 3 destructores, 4 fragatas
        initializeShips();

        // Colocar barcos aleatoriamente
        final Random random = new Random();
        int x, y;

        for (Ship ship : ships) {
            do {
                x = random.nextInt(BOARD_SIZE);
                y = random.nextInt(BOARD_SIZE);
            } while (!this.putShip(x, y, ship));

            ship.setPosition(x, y);
        }

        sunkShips = 0;
    }

    /**
     * Initializes the fleet according to game specifications.
     */
    public void initializeShips() {
        Map<Ship.Type, Integer> shipTypes = new HashMap<>();
        shipTypes.put(Ship.Type.CARRIER, 1);      // 1 portaaviones
        shipTypes.put(Ship.Type.SUBMARINE, 2);    // 2 submarinos
        shipTypes.put(Ship.Type.DESTROYER, 3);    // 3 destructores
        shipTypes.put(Ship.Type.FRIGATE, 4);      // 4 fragatas

        for (Map.Entry<Ship.Type, Integer> entry : shipTypes.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                ships.add(new Ship(entry.getKey()));
            }
        }
    }

    /**
     * Places a ship on the board at specified coordinates.
     *
     * @param x X coordinate (column)
     * @param y Y coordinate (row)
     * @param ship Ship to place
     * @return true if placed successfully, false if position invalid
     */
    public boolean putShip(int x, int y, Ship ship) {
        Ship.Direction direction = ship.getDirection();
        int length = ship.getLength();
        int c, r;

        if (validatePosition(x, y, ship)) {
            for (int i = 0; i < length; i++) {
                r = direction == Ship.Direction.VERTICAL ? y + i : y;
                c = direction == Ship.Direction.HORIZONTAL ? x + i : x;

                board.get(r).set(c, State.OCCUPIED);
            }
            return true;
        }

        return false;
    }

    /**
     * Validates if a ship can be placed at the specified position.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param ship Ship to validate
     * @return true if position is valid, false otherwise
     */
    public boolean validatePosition(int x, int y, Ship ship) {
        Ship.Direction direction = ship.getDirection();
        int length = ship.getLength();
        int c, r;

        for (int i = 0; i < length; i++) {
            r = direction == Ship.Direction.VERTICAL ? y + i : y;
            c = direction == Ship.Direction.HORIZONTAL ? x + i : x;

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
     * @param x X coordinate
     * @param y Y coordinate
     * @param ship Ship to remove
     */
    public void removeShip(int x, int y, Ship ship) {
        Ship.Direction direction = ship.getDirection();
        int length = ship.getLength();
        int c, r;

        for (int i = 0; i < length; i++) {
            r = direction == Ship.Direction.VERTICAL ? y + i : y;
            c = direction == Ship.Direction.HORIZONTAL ? x + i : x;

            board.get(r).set(c, State.EMPTY);
        }
    }

    /**
     * Gets the state of a specific cell.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return Cell state
     */
    public State getState(int x, int y) {
        return board.get(y).get(x);
    }

    /**
     * Changes the state of a cell.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param newState New state
     */
    public void changeState(int x, int y, State newState) {
        board.get(y).set(x, newState);
    }

    /**
     * Checks if a cell has already been shot (water, hit or sunk).
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if already shot
     */
    public boolean isWaterHitOrSunk(int x, int y) {
        State state = board.get(y).get(x);
        return state == State.WATER || state == State.HIT || state == State.SUNK;
    }

    /**
     * Checks if a cell is in HIT or SUNK state.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if hit or sunk
     */
    public boolean isHitOrSunk(int x, int y) {
        State state = board.get(y).get(x);
        return state == State.HIT || state == State.SUNK;
    }

    /**
     * Updates ship state to HIT when it receives a shot.
     *
     * @param x X coordinate of the shot
     * @param y Y coordinate of the shot
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

            // Verificar si todas las partes del barco fueron golpeadas
            for (int i = 0; i < length; i++) {
                int currentX = direction == Ship.Direction.HORIZONTAL ? shipX + i : shipX;
                int currentY = direction == Ship.Direction.VERTICAL ? shipY + i : shipY;

                if (board.get(currentY).get(currentX) != State.HIT) {
                    allHit = false;
                    break;
                }
            }

            // Si todas las partes fueron golpeadas, marcar como hundido
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
     * Checks if all ships on the board have been sunk.
     *
     * @return true if all ships are sunk
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
     * Gets a ship by its index.
     *
     * @param i Ship index
     * @return Ship at position i
     */
    public Ship getShip(int i) {
        return ships.get(i);
    }

    /**
     * Gets the complete list of ships.
     *
     * @return List of ships
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Prints the board to console (useful for debugging).
     */
    public void getBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board.get(i).get(j) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Gets the username.
     *
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username New username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the number of sunk ships.
     *
     * @return Number of sunk ships
     */
    public int getSunkShips() {
        return sunkShips;
    }

    /**
     * Increments the sunk ships counter.
     */
    public void setSunkShips() {
        this.sunkShips += 1;
    }

    /**
     * Gets the board size.
     *
     * @return Board size (10)
     */
    public int getBoardSize() {
        return BOARD_SIZE;
    }
}