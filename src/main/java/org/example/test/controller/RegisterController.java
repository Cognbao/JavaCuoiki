package org.example.test.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.model.User;
import org.example.test.service.AuthService;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    private AuthService authService;
    private Stage primaryStage;
    private Runnable onSuccess;

    public RegisterController(AuthService authService, Stage primaryStage, Runnable onSuccess) {
        this.authService = authService;
        this.primaryStage = primaryStage;
        this.onSuccess = onSuccess;
    }

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match");
                return;
            }

            User user = new User(username, password);
            if (authService.registerUser(user)) {
                onSuccess.run();
                primaryStage.close();
            } else {
                System.out.println("Registration failed");
            }
        });
    }
}