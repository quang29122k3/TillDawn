module com.example.libarymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires fontawesomefx;
    requires java.desktop;
    requires com.google.protobuf;
    requires java.mail;

    opens com.example.libarymanagementsystem to javafx.fxml;
    exports com.example.libarymanagementsystem;
}