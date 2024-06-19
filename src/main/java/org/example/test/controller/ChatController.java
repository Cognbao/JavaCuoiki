package org.example.test.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.test.main.ChatApp;
import org.example.test.model.ChatModel;
import org.example.test.service.AuthService;

public class ChatController {
    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;

    private ChatModel model;
    private String username;

    public ChatController(ChatModel model) {
        this.model = model;
    }

    @FXML
    private void initialize() {
        AuthService authService = ChatApp.getAuthService();
        if (authService != null) {
            this.username = authService.getCurrentUser();
        } else {
            System.err.println("Error: AuthService is not available.");
            return; // Or handle the error appropriately
        }

        // Initialize the ListView items after the view is loaded
        messageListView.setItems(model.getMessages());

        // Load message history and print for debugging
        model.loadMessageHistory();
        System.out.println("Messages loaded: " + model.getMessages());

        sendButton.setOnAction(event -> {
            String messageText = inputField.getText().trim();
            if (!messageText.isEmpty()) {
                String username = ChatApp.getAuthService() != null ? ChatApp.getAuthService().getCurrentUser() : "Unknown";

                try {
                    model.sendMessage(username, messageText);
                    System.out.println("Message sent to model: " + messageText); // Updated log message
                } catch (Exception e) {
                    System.err.println("Error sending message: " + e.getMessage());
                    e.printStackTrace();
                }
                inputField.clear();
            }
        });
    }

    public Button getSendButton() {
        return sendButton;
    }

    public TextField getInputField() {
        return inputField;
    }
}
