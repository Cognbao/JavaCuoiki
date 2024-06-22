package org.example.test.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.main.ChatApp;
import org.example.test.service.AuthService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    // Dependencies
    private AuthService authService;
    private Stage primaryStage;
    private Consumer<String> onSuccessfulLogin;

    // No-argument constructor required by FXMLLoader
    public LoginController() {
    }

    // Constructor with dependency injection
    public LoginController(AuthService authService, Stage primaryStage, Consumer<String> onSuccessfulLogin) {
        this.authService = authService;
        this.primaryStage = primaryStage;
        this.onSuccessfulLogin = onSuccessfulLogin;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnAction(event -> {
            try {
                handleLogin(event);
            } catch (SQLException e) {
                e.printStackTrace(); // Or handle the exception in a more user-friendly way
            }
        });

        registerButton.setOnAction(event -> {
            handleRegister(event);
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) throws SQLException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please fill in both username and password.");
            return;
        }

        try {
            // Attempt Authentication
            if (authService.authenticateUser(username, password)) {
                onSuccessfulLogin.accept(username);
                primaryStage.close(); // Close the login window
            } else {
                // More specific error message
                showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "A database error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Switch to registration screen
        ChatApp.getInstance().showRegisterScreen();
        primaryStage.close();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    // Setters for dependency injection
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setOnSuccess(Consumer<String> onSuccessfulLogin) {
        this.onSuccessfulLogin = onSuccessfulLogin;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
