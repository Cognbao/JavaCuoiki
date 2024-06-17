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
        System.out.println("Messages loaded: " + model.getMessages());  // Print for debugging

        messageListView.setItems(model.getMessages());
        System.out.println("ListView items set: " + model.getMessages().size()); // Print size

        sendButton.setOnAction(event -> {
            String messageText = inputField.getText();

            try {
                model.sendMessage("username", messageText); // Replace "username" with the actual username
                System.out.println("Message sent to model."); // Log successful send
            } catch (Exception e) { // Catch any exceptions during sending
                System.err.println("Error sending message: " + e.getMessage());
            }

            inputField.clear();
        });
    }
}

