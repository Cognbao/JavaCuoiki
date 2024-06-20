package org.example.test.controller;

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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
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

    @FXML
    private AuthService authService;

    private Stage primaryStage;
    private Runnable onSuccess;

    public RegisterController() {

    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
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

        // Attempt Registration
        if (authService.registerUser(new User(username, password))) { // Assuming the User constructor takes username and password
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "You have successfully registered. Please log in.");
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();
            showLoginScreen();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Username is already taken.");
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
    @FXML
    private void initialize(URL url, ResourceBundle resourceBundle) {
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