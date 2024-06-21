package org.example.test.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.test.controller.ChatController;
import org.example.test.controller.LoginController;
import org.example.test.model.ChatModel;
import org.example.test.network.Client;
import org.example.test.service.AuthService;

import java.io.IOException;

public class ChatApp extends Application {
    private static Stage stage;
    private AuthService authService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        authService = new AuthService();
        showLoginScreen();
    }


    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/LoginView.fxml"));
            System.out.println("Loading LoginView.fxml..."); // Debug statement
            Parent root = loader.load();
            System.out.println("LoginView.fxml loaded successfully."); // Debug statement
            LoginController loginController = loader.getController();
            loginController.setAuthService(authService);
            loginController.setPrimaryStage(stage);
            loginController.setOnSuccess(this::showChatScreen);
            System.out.println("LoginController initialized."); // Debug statement

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }




    private void showChatScreen(String username) {
        try {
            ChatModel model = new ChatModel(); // Create the model once for all instances

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/ChatView.fxml"));
            loader.setControllerFactory(c -> new ChatController(model)); // Pass the model to all controllers
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 500);
            Stage chatStage = new Stage();
            chatStage.setTitle("Chat App");
            chatStage.setScene(scene);
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeNetwork(ChatModel model, ChatController controller) {
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
    }
}
