module org.example.Battleship {
    requires javafx.controls;
    requires javafx.fxml;

    // Abrir paquetes para JavaFX FXML
    opens org.example.Battleship.Controllers to javafx.fxml;
    opens org.example.Battleship.Views to javafx.fxml;

    // Exportar paquetes principales
    exports org.example.Battleship;
    exports org.example.Battleship.Controllers;
    exports org.example.Battleship.Models;
    exports org.example.Battleship.Views;
}