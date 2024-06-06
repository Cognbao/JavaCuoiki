package org.example.test.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ChatView {
    private ListView<String> messageListView;
    private TextField inputField;
    private Button sendButton;
    private BorderPane root;

    public ChatView(Stage primaryStage) {
        primaryStage.setTitle("Chat App");

        messageListView = new ListView<>();
        inputField = new TextField();
        sendButton = new Button("Send");

        HBox inputBox = new HBox();
        inputBox.getChildren().addAll(inputField, sendButton);

        root = new BorderPane();
        root.setCenter(messageListView);
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ListView<String> getMessageListView() {
        return messageListView;
    }

    public TextField getInputField() {
        return inputField;
    }

    public Button getSendButton() {
        return sendButton;
    }
}