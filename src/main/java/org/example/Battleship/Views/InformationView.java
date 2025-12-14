package org.example.Battleship.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @author Javier Giraldo
 * @author Sebastian Niño
 * @author Pablo Arias
 * @version 1.0
 */
public class InformationView {

    private static InformationView instance;
    private Stage stage;

    private InformationView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Battleship/information-view.fxml"));
        Parent root = loader.load();

        stage = new Stage();
        stage.setTitle("Batalla Naval");
        stage.setScene(new Scene(root, 734, 730));
        stage.setResizable(false);
    }

    public static InformationView getInstance() throws IOException {
        if (instance == null) {
            instance = new InformationView();
        }
        return instance;
    }

    public void show() {
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
    }
}