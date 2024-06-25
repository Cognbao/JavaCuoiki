module chat.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires com.google.protobuf;
    requires java.desktop;
    requires jbcrypt;
    requires mysql.connector.java;

    exports org.example.test.main;
    exports org.example.test.controller;
    exports org.example.test.model;
    exports org.example.test.network;
    exports org.example.test.view;
    exports org.example.test.service;

    opens org.example.test.controller to javafx.fxml;
    opens org.example.test.view to javafx.fxml;
    exports org.example.test.util;


}