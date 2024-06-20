package org.example.test.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.example.test.model.ChatModel;
import org.example.test.view.ChatView;

public class ChatController {
    @FXML
    public BorderPane root;
    @FXML
    public TextField recipientField;
    @FXML
    public ComboBox recipientComboBox;
    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;

    private ChatModel model;

    // Constructor (inject the ChatModel)
    public ChatController(ChatModel model) {
        this.model = model;
    }

    public ChatController(ChatModel model, ChatView view) {

    }

    @FXML
    private void initialize() {
        // Bind the ListView items to the ObservableList in the ChatModel
        messageListView.setItems(model.getMessages());

        // Load message history when initializing
        model.loadMessageHistory();
        System.out.println("Messages loaded: " + model.getMessages());

        // Send button action handler
        sendButton.setOnAction(event -> {
            String messageText = inputField.getText();
            if (!messageText.isEmpty()) { // Check if message is not empty
                try {
                    model.sendMessage("username", messageText); // Replace "username" with actual value
                    System.out.println("Message sent to model.");
                } catch (Exception e) {
                    System.err.println("Error sending message: " + e.getMessage());
                    e.printStackTrace();
                }
                inputField.clear();
            }
        });
    }

    // Getters (if needed for other parts of your application)
    public Button getSendButton() {
        return sendButton;
    }

    public TextField getInputField() {
        return inputField;
    }
}
