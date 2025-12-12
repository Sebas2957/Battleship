package org.example.Battleship.Models.utilities;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for object serialization and game file management.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class serialization {

    private static Path relativePath = Paths.get("objectsSerialization.txt");
    private static final String STATS_FILE = "playerStats.txt";

    /**
     * Gets the serialization file path.
     *
     * @return File path as String
     */
    public static String getRelativePath() {
        return relativePath.toString();
    }

    /**
     * Default constructor.
     */
    public serialization() {}

    /**
     * Serializes objects to a file.
     * Used to save game boards.
     *
     * @param fileName File name
     * @param objects Objects to serialize
     */
    public static void serializeObjects(String fileName, Object... objects) {
        String path = getRelativePath();
        File file = new File(path);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            for (Object obj : objects) {
                oos.writeObject(obj);
            }

            System.out.println("Objetos serializados correctamente en: " + path);

        } catch (IOException e) {
            System.err.println("Error al serializar objetos: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes objects from a file.
     * Used to load saved boards.
     *
     * @param fileName File name
     * @return List of deserialized objects
     */
    public static List<Object> deserializeObjects(String fileName) {
        List<Object> objects = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            while (true) {
                try {
                    Object obj = ois.readObject();
                    objects.add(obj);
                } catch (EOFException e) {
                    break; // Fin del archivo
                }
            }

            System.out.println("Objetos deserializados correctamente: " + objects.size() + " objetos");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar objetos: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return objects;
    }

    /**
     * Saves player statistics to a plain text file.
     *
     * @param nickname Player name
     * @param playerSunkShips Player's sunk ships
     * @param machineSunkShips Machine's sunk ships
     */
    public static void savePlayerStats(String nickname, int playerSunkShips, int machineSunkShips) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATS_FILE))) {
            writer.write("ESTADÍSTICAS DEL JUEGO");
            writer.newLine();
            writer.write("======================");
            writer.newLine();
            writer.write("Jugador: " + nickname);
            writer.newLine();
            writer.write("Barcos hundidos del jugador: " + playerSunkShips);
            writer.newLine();
            writer.write("Barcos hundidos de la máquina: " + machineSunkShips);
            writer.newLine();
            writer.write("Total de barcos: 10");
            writer.newLine();

            System.out.println("Estadísticas guardadas en: " + STATS_FILE);

        } catch (IOException e) {
            System.err.println("Error al guardar estadísticas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads player statistics from plain text file.
     *
     * @return File content as String
     */
    public static String loadPlayerStats() {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(STATS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            System.out.println("Estadísticas cargadas correctamente");

        } catch (FileNotFoundException e) {
            System.out.println("No se encontró archivo de estadísticas");
            return null;
        } catch (IOException e) {
            System.err.println("Error al leer estadísticas: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return content.toString();
    }

    /**
     * Clears a file without deleting it.
     *
     * @param fileName File name to clear
     * @throws IOException If I/O error occurs
     */
    public static void clearFile(String fileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            System.out.println("Archivo limpiado: " + fileName);
        }

        // También limpiar el archivo de estadísticas
        try (FileOutputStream fos = new FileOutputStream(STATS_FILE)) {
            System.out.println("Archivo de estadísticas limpiado");
        }
    }

    /**
     * Checks if a valid save file exists.
     *
     * @return true if exists and has content, false otherwise
     */
    public static boolean hasSavedGame() {
        File file = new File(getRelativePath());
        return file.exists() && file.length() > 0;
    }
}