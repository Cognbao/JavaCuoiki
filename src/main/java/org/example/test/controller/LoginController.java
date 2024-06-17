package org.example.test.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.test.service.AuthService;

public class LoginController {
    private final AuthService authService;
    private final Stage primaryStage;
    private final Runnable onSuccess;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;

    public LoginController(AuthService authService, Stage primaryStage, Runnable onSuccess) {
        this.authService = authService;
        this.primaryStage = primaryStage;
        this.onSuccess = onSuccess;
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (authService.authenticateUser(username, password)) {
                onSuccess.run();    // Call the method to show the chat screen
                primaryStage.close();
            } else {
                System.out.println("Authentication failed");
            }
        });
    }
}
