package org.example.test.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.test.network.Client;
import org.example.test.service.MessageService;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatModel {

    private static final Logger logger = Logger.getLogger(ChatModel.class.getName());

    private final ObservableList<String> messages = FXCollections.observableArrayList();
    private MessageService messageService; // Assuming you have this class
    private Consumer<Message> newMessageListener;
    private Client client;

    public ChatModel(Client client) {
        this.client = client;
        messageService = new MessageService(); // Initialize your MessageService
        messageService.registerNewMessageListener(this::onNewMessage);
    }

    public ObservableList<String> getMessages() {
        return messages;
    }

    public void sendMessage(String username, String content) {
        sendPrivateMessage(username, null, content);
    }

    public void sendPrivateMessage(String username, String recipient, String content) {
        Message message = new Message(username, recipient, content);
        try {
            Client.getInstance().sendMessage(content, recipient);
            // After successfully sending, call the listener with the new message
            onNewMessage(message);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending message: " + e.getMessage(), e);
            // Handle the error, e.g., display an error message to the user.
        }
    }

    // Load initial message history
    public void loadMessageHistory() {
        loadMessageHistory(null); // Load all messages (both public and private) initially
    }

    // Load message history for a specific recipient (null for all messages)
    public void loadMessageHistory(String recipient) {
        List<String> history = messageService.getMessageHistory(recipient);
        if (history != null) {
            messages.clear();
            messages.addAll(history);
        } else {
            messages.clear();
        }
    }

    //Register a listener to receive new message notification.
    public void addNewMessageListener(Consumer<Message> listener) {
        this.newMessageListener = listener;
    }

    //Invoke the listener when a new message is received.
    private void onNewMessage(Message message) {
        if (newMessageListener != null) {
            Platform.runLater(() -> newMessageListener.accept(message));
        }
    }
}

