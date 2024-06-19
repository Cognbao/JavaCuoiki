package org.example.test.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.test.controller.ChatController;
import org.example.test.controller.LoginController;
import org.example.test.controller.RegisterController;
import org.example.test.model.ChatModel;
import org.example.test.network.Client;
import org.example.test.service.AuthService;

import java.io.IOException;

public class ChatApp extends Application {

    private static ChatModel model;
    private static AuthService authService = new AuthService(); // Initialize AuthService

    @Override
    public void start(Stage primaryStage) {
        try {
            AuthService authService = new AuthService();
            showLoginScreen(primaryStage, authService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLoginScreen(Stage primaryStage, AuthService authService) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/LoginView.fxml"));
        loader.setControllerFactory(c -> new LoginController(authService, primaryStage, this::showChatScreen));
        Parent root = loader.load();
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");

        // Add a button for registration
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> showRegisterScreen(primaryStage, authService));
        ((VBox) root).getChildren().add(registerButton);

        primaryStage.show();
    }

    private void showRegisterScreen(Stage primaryStage, AuthService authService) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/RegisterView.fxml"));
            loader.setControllerFactory(c -> new RegisterController(authService, primaryStage, () -> {
                try {
                    showLoginScreen(primaryStage, authService);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            Parent root = loader.load();
            Scene scene = new Scene(root, 300, 250);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Register");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showChatScreen() {
        try {
            String username = authService.getCurrentUser();  // Get username from AuthService
            model = new ChatModel();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/ChatView.fxml"));
            loader.setControllerFactory(c -> new ChatController(model));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 500);
            Stage chatStage = new Stage();
            chatStage.setTitle(username + "'s Chat"); // Set title with username
            chatStage.setScene(scene);
            chatStage.show();

            initializeNetwork(model, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get AuthService method
    public static AuthService getAuthService() {
        return authService;
    }

    private void initializeNetwork(ChatModel model, ChatController controller) {
        try {
            Client client = new Client("localhost", 12345);
            client.startListening(model); // Start listening in a separate thread

            controller.getSendButton().setOnAction(e -> {
                String message = controller.getInputField().getText();
                if (!message.isEmpty()) {
                    client.sendMessage(message);
                    model.addMessage("Me: " + message);
                    controller.getInputField().clear();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
