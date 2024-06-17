module chat.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    opens org.example.test.controller to javafx.fxml;
    exports org.example.test.main;
    exports org.example.test.controller;
    exports org.example.test.model;
    exports org.example.test.network;
    exports org.example.test.view;
    exports org.example.test.service;
}