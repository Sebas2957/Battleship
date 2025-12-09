package org.example.Battleship.Views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility class for displaying alerts and confirmation dialogs.
 *
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @version 1.0
 */
public class AlertBox {

    /**
     * Displays an informative alert.
     *
     * @param title Alert title
     * @param header Alert header
     * @param content Alert content
     * @param type Alert type
     */
    public void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog.
     *
     * @param title Dialog title
     * @param message Dialog message
     * @return true if user confirms, false if cancelled
     */
    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}