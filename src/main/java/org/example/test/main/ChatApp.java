package org.example.test.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.test.controller.ChatController;
import org.example.test.controller.LoginController;
import org.example.test.controller.RegisterController;
import org.example.test.model.ChatModel;
import org.example.test.network.Client;
import org.example.test.service.AuthService;
import org.example.test.view.ChatView;

import java.io.IOException;

public class ChatApp extends Application {
    private static Stage stage;
    private static ChatApp instance;
    private AuthService authService;


    public static ChatApp getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        stage = primaryStage;
        authService = new AuthService(); // Initialize your AuthService
        showLoginScreen();
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/LoginView.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setAuthService(authService);
            loginController.setStage(stage);

            // Call the non-static showChatScreen method
            loginController.setOnSuccess(this::showChatScreen);

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
            Parent root = loader.load();

            ChatController chatController = loader.getController();

            Client client = new Client(new ChatView(), "localhost", 12345, username);
            ChatModel chatModel = new ChatModel(client);

            chatController.setClient(client);
            chatController.setModel(chatModel);

            Scene scene = new Scene(root);
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

            // Start the client's listening thread
            new Thread(client).start();

        } catch (IOException e) {
            System.err.println("Failed to load chat screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showRegisterScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/RegisterView.fxml"));
            Parent root = loader.load();
            RegisterController registerController = loader.getController();
            registerController.setAuthService(authService);
            registerController.setPrimaryStage(stage);
            registerController.setOnSuccess(this::showLoginScreen);

            Scene scene = new Scene(root);
            Stage registerStage = new Stage();
            registerStage.setScene(scene);
            registerStage.show();
        } catch (IOException e) { // Catch IOException and SQLException
            System.err.println("Failed to load register screen: " + e.getMessage());
            e.printStackTrace();
            // Optionally, show an alert to the user if you're in a JavaFX Application Thread
            if (Platform.isFxApplicationThread()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("An error occurred while loading the registration screen.");
                    alert.showAndWait();
                });
            }
        }
    }
}
