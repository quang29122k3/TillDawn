module com.example.libarymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires fontawesomefx;
    requires mysql.connector.java;
    requires org.json;
    requires java.mail;
    requires jbcrypt;
    requires java.desktop;

    opens com.example.libarymanagementsystem to javafx.fxml;
    exports com.example.libarymanagementsystem;
    exports com.example.libarymanagementsystem.model;
    opens com.example.libarymanagementsystem.model to javafx.fxml;
    exports com.example.libarymanagementsystem.controller;
    opens com.example.libarymanagementsystem.controller to javafx.fxml;
}