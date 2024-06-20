package org.example.test.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.service.AuthService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    // Optional: Dependency injection through constructor
    private AuthService authService;
    private Stage primaryStage;
    private Consumer<String> onSuccess;

    // No-argument constructor required by FXMLLoader
    public LoginController() {}

    // Constructor with dependency injection
    public LoginController(AuthService authService, Stage primaryStage, Consumer<String> onSuccess) {
        this.authService = authService;
        this.primaryStage = primaryStage;
        this.onSuccess = onSuccess;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handler for the register button
        registerButton.setOnAction(this::handleRegister);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authService.authenticateUser(username, password)) {
            onSuccess.accept(username);
            primaryStage.close();
        } else {
            // Handle invalid credentials (e.g., display an error message)
            System.out.println("Invalid username or password.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
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
}
