package org.example.test.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.test.controller.ChatController;
import org.example.test.controller.LoginController;
import org.example.test.network.Client;
import org.example.test.service.AuthService;
import org.example.test.view.ChatView;

import java.io.IOException;

public class ChatApp extends Application {
    private static Stage stage; // Assuming you have this declared somewhere
    private AuthService authService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        authService = new AuthService(); // Initialize your AuthService
        showLoginScreen();
    }

    public void showLoginScreen() {
        try {
            // Load the FXML file for the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/LoginView.fxml"));
            Parent root = loader.load();

            // Get the controller for the login screen and set necessary services
            LoginController loginController = loader.getController();
            loginController.setAuthService(authService);
            loginController.setStage(stage);

            // Use a lambda expression to handle successful login
            loginController.setOnSuccess(username -> {
                showChatScreen(username); // Ensure showChatScreen is not static if called from an instance context
            });

            // Set the scene and show the login screen
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showChatScreen(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/ChatView.fxml"));
            Parent root = loader.load(); // Load the FXML first

            // Get the controller instance that FXMLLoader created
            ChatController chatController = loader.getController();

            // Now create Client instance after ChatView is loaded
            ChatView chatView = loader.getController();
            Client client = new Client(chatView, "localhost", 12345, username);
            chatView.setClient(client); // Set the client for the view

            // chatController.setView(chatView); this line is no longer needed

            Scene scene = new Scene(root, 400, 400);
            stage.setScene(scene);
            stage.setTitle("Chat App - " + username);
            stage.setOnCloseRequest(event -> {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stage.show();

            new Thread(client).start();

        } catch (IOException e) {
            System.err.println("Failed to load chat screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

