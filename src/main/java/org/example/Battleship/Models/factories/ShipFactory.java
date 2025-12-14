package org.example.Battleship.Models.factories;

import org.example.Battleship.Models.Ship;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating ships (Factory Design Pattern).
 * Centralizes ship creation in one place.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class ShipFactory {

    /**
     * Creates a ship of the specified type.
     *
     * @param type Type of ship to create
     * @return New ship
     */
    public static Ship createShip(Ship.Type type) {
        return new Ship(type);
    }

    /**
     * Creates the complete fleet according to game rules.
     * - 1 Carrier (4 cells)
     * - 2 Submarines (3 cells)
     * - 3 Destroyers (2 cells)
     * - 4 Frigates (1 cell)
     *
     * @return List with 10 ships
     */
    public static List<Ship> createFleet() {
        List<Ship> fleet = new ArrayList<>();

        // 1 Portaaviones
        fleet.add(new Ship(Ship.Type.CARRIER));

        // 2 Submarinos
        fleet.add(new Ship(Ship.Type.SUBMARINE));
        fleet.add(new Ship(Ship.Type.SUBMARINE));

        // 3 Destructores
        fleet.add(new Ship(Ship.Type.DESTROYER));
        fleet.add(new Ship(Ship.Type.DESTROYER));
        fleet.add(new Ship(Ship.Type.DESTROYER));

        // 4 Fragatas
        fleet.add(new Ship(Ship.Type.FRIGATE));
        fleet.add(new Ship(Ship.Type.FRIGATE));
        fleet.add(new Ship(Ship.Type.FRIGATE));
        fleet.add(new Ship(Ship.Type.FRIGATE));

        return fleet;
    }

    /**
     * Gets the Spanish name of the ship type.
     *
     * @param type Ship type
     * @return Name in Spanish
     */
    public static String getShipName(Ship.Type type) {
        switch (type) {
            case CARRIER:
                return "Portaaviones";
            case SUBMARINE:
                return "Submarino";
            case DESTROYER:
                return "Destructor";
            case FRIGATE:
                return "Fragata";
            default:
                return "Desconocido";
        }
    }
}