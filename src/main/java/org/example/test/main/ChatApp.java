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

    @Override
    public void start(Stage primaryStage) {
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

            // Use a lambda expression to adapt the showChatScreen method
            loginController.setOnSuccess(() -> { //use Runnable instead of Consumer<String>
                showChatScreen(loginController.getUsername());
            });

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showChatScreen(String username) { // This method is not static
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/ChatView.fxml"));
            Parent root = loader.load();

            ChatView chatView = loader.getController();
            Client client = new Client(chatView, "localhost", 12345, username);
            chatView.setClient(client);

            ChatController chatController = loader.getController();
            chatController.setView(chatView);
            chatController.setClient(client);
            chatController.setPrimaryStage(stage);
            chatController.setOnClose(() -> {
                showLoginScreen(); // Now the lambda calls showLoginScreen without arguments
            });
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Chat App - " + username);
            stage.show();

            // Start the client's listening thread
            new Thread(client).start();

        } catch (IOException e) {
            System.err.println("Failed to load chat screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
