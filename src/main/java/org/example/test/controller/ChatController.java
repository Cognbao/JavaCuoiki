package org.example.test.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.test.model.ChatModel;

public class ChatController {
    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;

    private ChatModel model;

    public ChatController(ChatModel model) {
        this.model = model;
        this.model.messageProperty().addListener((obs, oldMessage, newMessage) -> {
            messageListView.getItems().add(newMessage);
        });
    }

    public Button getSendButton() {
        return sendButton;
    }

    public TextField getInputField() {
        return inputField;
    }

    @FXML
    private void initialize() {
        model.loadMessageHistory();

        sendButton.setOnAction(event -> {
            String messageText = inputField.getText();
            model.sendMessage("username", messageText); // Replace "username" with the actual username
            inputField.clear();
        });
    }
}

