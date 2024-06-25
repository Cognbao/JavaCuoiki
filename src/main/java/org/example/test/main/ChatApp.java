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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatApp extends Application {
    private static final Logger logger = Logger.getLogger(ChatApp.class.getName());
    private static Stage stage;
    private static ChatApp instance;
    private AuthService authService;

    public static ChatApp getInstance() {
        return instance;
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
            logger.log(Level.SEVERE, "Failed to load login screen", e);
            showErrorAlert("Failed to load login screen: " + e.getMessage());
        }
    }

    public void showChatScreen(String username) {
        try {
            logger.info("Initializing chat screen for user: " + username);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/ChatView.fxml"));
            Parent root = loader.load();

            // Get ChatController from FXMLLoader
            ChatController chatController = loader.getController();

            // Initialize your model and client
            Client client = new Client(chatController, "localhost", 12345, username); // Assuming Client takes ChatController
            ChatModel chatModel = new ChatModel(client);

            // Set model and client in ChatController
            chatController.setClient(client);
            chatController.setModel(chatModel);

            // Set up the stage and scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Chat App - " + username);
            stage.setOnCloseRequest(event -> {
                client.disconnect();
            });
            stage.show();

            // Start client's listening thread
            client.startListening();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load chat screen", e);
            showErrorAlert("Failed to load chat screen: " + e.getMessage());
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
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load register screen", e);
            showErrorAlert("Failed to load register screen: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        // Set global exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.log(Level.SEVERE, "Unhandled exception in thread " + thread, throwable);
        });
        launch(args);
    }
}
