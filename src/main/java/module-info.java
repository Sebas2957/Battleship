module org.example.Battleship {
    requires javafx.controls;
    requires javafx.fxml;

    // Abrir paquetes para JavaFX
    opens org.example.Battleship.Controllers to javafx.fxml;
    opens org.example.Battleship.Views to javafx.fxml;

    // Exportar paquetes
    exports org.example.Battleship;
    exports org.example.Battleship.Controllers;
    exports org.example.Battleship.Models;
    exports org.example.Battleship.Models.utilities;
    exports org.example.Battleship.Models.exceptions;
    exports org.example.Battleship.Models.structures;
    exports org.example.Battleship.Models.factories;
    exports org.example.Battleship.Models.interfaces;
    exports org.example.Battleship.Models.observers;
    exports org.example.Battleship.Views;
}