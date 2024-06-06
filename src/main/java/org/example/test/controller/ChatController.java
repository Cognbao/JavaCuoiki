package org.example.test.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.test.model.ChatModel;

public class ChatController {
    private final ChatModel model;
    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;

    public ChatController(ChatModel model) {
        this.model = model;
    }

    @FXML
    private void initialize() {
        // Initialize the controller, if needed
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