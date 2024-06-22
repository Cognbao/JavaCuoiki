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
import org.example.test.main.ChatApp;
import org.example.test.model.User;
import org.example.test.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

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

    // Constructor
    public RegisterController() {
        authService = new AuthService(); // Initialize AuthService here or use DI
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
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

        // Additional validation (e.g., username length, password strength) could be added here

        try {
            // Hash the password before storing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Attempt Registration
            if (authService.registerUser(new User(username, hashedPassword))) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Success",
                        "You have successfully registered. Please log in.");
                Stage currentStage = (Stage) registerButton.getScene().getWindow();
                currentStage.close(); // Close registration window
                // Open the login screen after a successful registration
                ChatApp.getInstance().showLoginScreen();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Error",
                        "Username is already taken or another error occurred.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "A database error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Optional method to display the login screen
    private void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/LoginView.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setAuthService(authService);

            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
