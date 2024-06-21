package org.example.test.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.model.User;
import org.example.test.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

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

    // No-argument constructor
    public RegisterController() {
        // If not using dependency injection, initialize authService here
        authService = new AuthService();
    }

    // Constructor with dependency injection
    public RegisterController(AuthService authService, Stage primaryStage, Runnable onSuccess) {
        this.authService = authService;
        this.primaryStage = primaryStage;
        this.onSuccess = onSuccess;
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic (if needed)
    }

    @FXML
    public void handleRegister(ActionEvent event) throws SQLException {
        String username = usernameField.getText().trim(); // Trim to remove leading/trailing spaces
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText();

        // Input Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match.");
            return;
        }

        // Hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Attempt Registration
        if (authService.registerUser(new User(username, hashedPassword))) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "You have successfully registered. Please log in.");

            // Close the registration window
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();

            // Show the login screen
            if (onSuccess != null) {
                onSuccess.run(); // Call the onSuccess callback to show the login screen
            } else {
                showLoginScreen();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Username is already taken or another error occurred.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Setters for dependency injection if needed
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }
}