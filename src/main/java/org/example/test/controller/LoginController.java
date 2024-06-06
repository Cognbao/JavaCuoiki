package org.example.test.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.model.UserModel;
import org.example.test.main.ChatApp;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private UserModel userModel;
    private Stage primaryStage;

    public LoginController(UserModel userModel, Stage primaryStage) {
        this.userModel = userModel;
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (userModel.authenticate(username, password)) {
            ChatApp.showChatView(primaryStage, userModel);
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }
}