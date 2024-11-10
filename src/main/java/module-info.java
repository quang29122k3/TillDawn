module com.example.libarymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires fontawesomefx;

    opens com.example.libarymanagementsystem to javafx.fxml;
    exports com.example.libarymanagementsystem;
}