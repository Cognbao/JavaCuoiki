package org.example.test.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.test.model.UserModel;
import org.example.test.model.ChatModel;
import org.example.test.controller.ChatController;
import org.example.test.controller.LoginController;

import java.io.IOException;
import java.util.Objects;

public class ChatApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        UserModel userModel = new UserModel();
        showLoginView(primaryStage, userModel);
    }

    public static void showLoginView(Stage primaryStage, UserModel userModel) {
        try {
            FXMLLoader loader = new FXMLLoader(ChatApp.class.getResource("/org/example/test/fxml/LoginView.fxml"));
            loader.setControllerFactory(c -> new LoginController(userModel, primaryStage));
            Parent root = loader.load();

            Scene scene = new Scene(root, 300, 200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showChatView(Stage primaryStage, UserModel userModel) {
        try {
            ChatModel chatModel = new ChatModel();
            FXMLLoader loader = new FXMLLoader(ChatApp.class.getResource("/org/example/test/fxml/ChatView.fxml"));
            loader.setControllerFactory(c -> new ChatController(chatModel));
            Parent root = loader.load();

            Scene scene = new Scene(root, 500, 500);
            //String css = Objects.requireNonNull(ChatApp.class.getResource("/styles.css")).toExternalForm();
            //scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Chat App");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}