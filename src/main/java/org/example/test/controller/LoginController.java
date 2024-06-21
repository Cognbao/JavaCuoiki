package org.example.test.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.service.AuthService;

import java.io.IOException;
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

    // Dependencies (to be injected)
    private AuthService authService;
    private Stage primaryStage;
    private Consumer<String> onSuccessfulLogin;
    private Consumer<String> onSuccess;

    // No-argument constructor required by FXMLLoader
    public LoginController() {}

    // Constructor with dependency injection
    public LoginController(AuthService authService, Stage primaryStage, Consumer<String> onSuccessfulLogin) {
        this.authService = authService;
        this.primaryStage = primaryStage;
        this.onSuccessfulLogin = onSuccessfulLogin;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // This method is called after the FXML file has been loaded
        // You can initialize elements and set up event handlers here

        loginButton.setOnAction(this::handleLogin);
        registerButton.setOnAction(this::handleRegister);
    }
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please fill in both username and password.");
            return;
        }

        try {
            // Attempt Authentication
            if (authService != null && authService.authenticateUser(username, password)) {
                onSuccessfulLogin.accept(username);
                primaryStage.close();
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
        System.out.println("Register button clicked"); // Debug statement
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/RegisterView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage registerStage = new Stage();
            registerStage.setScene(scene);
            registerStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load register screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Setters for dependency injection
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setOnSuccess(Consumer<String> onSuccess) {
        this.onSuccess = onSuccess;
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
