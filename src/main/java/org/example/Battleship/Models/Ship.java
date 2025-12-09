package org.example.Battleship.Models;

import javafx.scene.shape.*;
import java.io.Serializable;
import java.util.Random;

/**
 * Represents a ship in the Battleship game.
 * Manages type, position, direction and hit state.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @version 1.0
 */
public class Ship implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Ship types according to specifications.
     */
    public enum Type {
        CARRIER,    // 4 casillas
        SUBMARINE,  // 3 casillas
        DESTROYER,  // 2 casillas
        FRIGATE     // 1 casilla
    }

    /**
     * Ship direction on the board.
     */
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private int tailX;
    private int tailY;
    private int length;
    private int hits;
    private Direction direction;
    private Type type;

    /**
     * Constructor that creates a ship of specified type with random direction.
     *
     * @param type Ship type to create
     */
    public Ship(Type type) {
        this.type = type;
        Random random = new Random();

        // Asignar dirección aleatoria
        direction = Direction.values()[random.nextInt(Direction.values().length)];

        // Asignar longitud según el tipo
        switch (type) {
            case CARRIER:
                this.length = 4;
                break;
            case SUBMARINE:
                this.length = 3;
                break;
            case DESTROYER:
                this.length = 2;
                break;
            case FRIGATE:
                this.length = 1;
                break;
        }

        this.hits = 0;
    }

    /**
     * Rotates the ship 90 degrees.
     */
    public void rotate() {
        direction = direction == Direction.HORIZONTAL ? Direction.VERTICAL : Direction.HORIZONTAL;
    }

    /**
     * Checks if given coordinates are within the ship.
     *
     * @param x X coordinate to check
     * @param y Y coordinate to check
     * @return true if coordinates are in the ship
     */
    public boolean checkPosition(int x, int y) {
        return tailX <= x && tailX + getWidth() - 1 >= x &&
                tailY <= y && tailY + getHeight() - 1 >= y;
    }

    /**
     * Increments the hits counter.
     */
    public void incrementHits() {
        this.hits++;
    }

    /**
     * Checks if the ship is sunk.
     *
     * @return true if hits >= length
     */
    public boolean isSunk() {
        return hits >= length;
    }

    /**
     * Gets the graphical representation of the ship.
     *
     * @return Path with the ship figure
     */
    public Path getDraw() {
        switch (this.type) {
            case FRIGATE:
                return new Path(
                        new MoveTo(0, 0),
                        new LineTo(20, 4),
                        new ArcTo(250, 40, 0, 20, 36, false, true),
                        new LineTo(0, 40),
                        new ClosePath(),
                        new MoveTo(5, 7),
                        new LineTo(20, 7),
                        new LineTo(20, 12),
                        new LineTo(5, 12),
                        new ClosePath(),
                        new MoveTo(5, 15),
                        new LineTo(20, 15),
                        new LineTo(20, 20),
                        new LineTo(5, 20),
                        new ClosePath(),
                        new MoveTo(5, 23),
                        new LineTo(20, 23),
                        new LineTo(20, 33),
                        new LineTo(5, 33),
                        new ClosePath(),
                        new MoveTo(23, 20),
                        new LineTo(23, 9),
                        new ArcTo(370, 40, 0, 23, 31, false, true),
                        new ClosePath(),
                        new LineTo(36, 20)
                );

            case DESTROYER:
                return new Path(
                        new MoveTo(0, 6),
                        new LineTo(10, 0),
                        new LineTo(40, 0),
                        new ArcTo(80, 100, 0, 80, 20, false, true),
                        new ArcTo(80, 100, 0, 40, 40, false, true),
                        new LineTo(10, 40),
                        new LineTo(0, 34),
                        new ClosePath(),
                        new MoveTo(15, 6),
                        new LineTo(43, 6),
                        new ArcTo(80, 100, 0, 72, 20, false, true),
                        new ArcTo(80, 100, 0, 43, 34, false, true),
                        new LineTo(9, 34),
                        new ArcTo(5, 5, 0, 6, 30, false, true),
                        new LineTo(6, 10),
                        new ArcTo(5, 5, 0, 10, 6, false, true),
                        new ClosePath(),
                        new MoveTo(15, 10),
                        new LineTo(25, 10),
                        new LineTo(25, 17.5),
                        new LineTo(15, 17.5),
                        new ClosePath(),
                        new MoveTo(15, 30),
                        new LineTo(25, 30),
                        new LineTo(25, 22.5),
                        new LineTo(15, 22.5),
                        new ClosePath(),
                        new MoveTo(31, 20),
                        new LineTo(31, 11),
                        new LineTo(45, 11),
                        new LineTo(45, 29),
                        new LineTo(31, 29),
                        new ClosePath(),
                        new MoveTo(45, 18.5),
                        new LineTo(60, 18.5),
                        new LineTo(60, 21.5),
                        new LineTo(45, 21.5),
                        new MoveTo(60, 18.5),
                        new LineTo(60, 17.5),
                        new LineTo(63, 17.5),
                        new LineTo(63, 22.5),
                        new LineTo(60, 22.5),
                        new ClosePath(),
                        new MoveTo(50, 17.5),
                        new LineTo(50, 8),
                        new MoveTo(50, 22.5),
                        new LineTo(50, 32)
                );

            case SUBMARINE:
                return new Path(
                        new MoveTo(0, 15),
                        new LineTo(40, 8),
                        new LineTo(102, 3),
                        new ArcTo(10, 10, 0, 102, 37, false, true),
                        new LineTo(40, 33),
                        new LineTo(0, 25),
                        new ClosePath(),
                        new MoveTo(6.25, 13),
                        new LineTo(2, 2),
                        new LineTo(12, 2),
                        new LineTo(25, 10),
                        new MoveTo(6.25, 27),
                        new LineTo(2, 38),
                        new LineTo(12, 38),
                        new LineTo(25, 30),
                        new MoveTo(6.25, 17),
                        new LineTo(25, 17),
                        new LineTo(25, 23),
                        new LineTo(6.25, 23),
                        new ClosePath(),
                        new MoveTo(100, 13),
                        new ArcTo(5, 5, 0, 100, 27, false, true),
                        new LineTo(90, 27),
                        new ArcTo(5, 5, 0, 90, 13, false, true),
                        new ClosePath()
                );

            case CARRIER:
                return new Path(
                        new MoveTo(0, 9),
                        new LineTo(26, 8),
                        new LineTo(36, 6),
                        new LineTo(70, 6),
                        new ArcTo(60, 10, 0, 78, 0, false, true),
                        new LineTo(118, 0),
                        new LineTo(124, 10),
                        new LineTo(160, 12),
                        new LineTo(158, 27),
                        new LineTo(128, 32),
                        new LineTo(123, 40),
                        new LineTo(36, 38),
                        new LineTo(26, 32),
                        new LineTo(2, 31),
                        new ArcTo(2, 15, 0, 0, 24, false, false),
                        new ClosePath(),
                        new MoveTo(26, 8),
                        new LineTo(120, 8),
                        new MoveTo(4, 20),
                        new LineTo(8, 20),
                        new MoveTo(12, 20),
                        new LineTo(16, 20),
                        new MoveTo(20, 20),
                        new LineTo(24, 20),
                        new MoveTo(28, 20),
                        new LineTo(32, 20),
                        new MoveTo(36, 20),
                        new LineTo(40, 20),
                        new MoveTo(44, 20),
                        new LineTo(48, 20),
                        new MoveTo(52, 20),
                        new LineTo(56, 20),
                        new MoveTo(60, 20),
                        new LineTo(64, 20),
                        new MoveTo(68, 20),
                        new LineTo(72, 20),
                        new MoveTo(76, 20),
                        new LineTo(80, 20),
                        new MoveTo(84, 20),
                        new LineTo(88, 20),
                        new MoveTo(92, 20),
                        new LineTo(96, 20),
                        new MoveTo(100, 20),
                        new LineTo(104, 20),
                        new MoveTo(108, 20),
                        new LineTo(112, 20),
                        new MoveTo(116, 20),
                        new LineTo(120, 20),
                        new MoveTo(124, 20),
                        new LineTo(128, 20),
                        new MoveTo(132, 20),
                        new LineTo(136, 20),
                        new MoveTo(140, 20),
                        new LineTo(144, 20),
                        new MoveTo(148, 20),
                        new LineTo(152, 20),
                        new MoveTo(40, 30),
                        new LineTo(44, 28),
                        new ArcTo(10, 10, 0, 46, 25, false, true),
                        new ArcTo(10, 10, 0, 48, 28, false, true),
                        new LineTo(52, 30),
                        new LineTo(52.25, 31),
                        new LineTo(48, 31.25),
                        new LineTo(52, 35),
                        new LineTo(41.25, 35),
                        new LineTo(44, 31.25),
                        new ClosePath(),
                        new MoveTo(60, 37),
                        new LineTo(60, 25.75),
                        new LineTo(112, 25.75),
                        new LineTo(112, 37),
                        new ClosePath(),
                        new MoveTo(70, 30),
                        new ArcTo(2.75, 2.75, 0, 70, 31, true, true),
                        new MoveTo(80, 30),
                        new ArcTo(2.75, 2.75, 0, 80, 31, true, true)
                );

            default:
                return null;
        }
    }

    // Getters y Setters

    public int getTailX() { return tailX; }
    public void setTailX(int tailX) { this.tailX = tailX; }

    public int getTailY() { return tailY; }
    public void setTailY(int tailY) { this.tailY = tailY; }

    public int getHits() { return hits; }
    public void setHits(int hits) { this.hits = hits; }

    public int getLength() { return length; }

    public Direction getDirection() { return direction; }
    public void setOrientation(Direction direction) { this.direction = direction; }

    public Type getType() { return type; }

    /**
     * Gets the ship height in cells.
     *
     * @return Ship height
     */
    public int getHeight() {
        return direction == Direction.VERTICAL ? length : 1;
    }

    /**
     * Gets the ship width in cells.
     *
     * @return Ship width
     */
    public int getWidth() {
        return direction == Direction.HORIZONTAL ? length : 1;
    }

    /**
     * Sets the ship position.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setPosition(int x, int y) {
        this.tailX = x;
        this.tailY = y;
    }
}