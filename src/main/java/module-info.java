module org.example.saejavafx {

    requires javafx.controls;
    requires javafx.fxml;

    exports org.example.Model;
    exports org.example.Vue;
    exports org.example.Controleur;

    opens org.example.Controleur to javafx.graphics;
}