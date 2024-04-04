module com.example.oopprojekt {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.oopprojekt to javafx.fxml;
    exports com.example.oopprojekt;
}