package org.example.test.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.test.network.Client;
import org.example.test.service.MessageService;
import org.example.test.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatModel {

    private static final Logger logger = Logger.getLogger(ChatModel.class.getName());

    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private MessageService messageService;
    private Consumer<Message> newMessageListener;
    private Client client;

    public ChatModel(Client client) {
        this.client = client;
        messageService = new MessageService();
        messageService.registerNewMessageListener(this::onNewMessage);
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void sendMessage(String username, String content) {
        sendPrivateMessage(username, null, content);
    }

    public void sendPrivateMessage(String username, String recipient, String content) {
        Message message = new Message(username, recipient, content);
        try {
            Client.getInstance().sendMessage(content, recipient);
            onNewMessage(message);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending message: " + e.getMessage(), e);
        }
    }

    public void loadMessageHistory() {
        List<Message> messageList = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE sender_id = ? OR recipient_id = ?")) {
            String username = client.getUsername();
            stmt.setString(1, username);
            stmt.setString(2, username);

            logger.info("Executing query to load message history for user: " + username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender_id");
                    String recipient = rs.getString("recipient_id");
                    String content = rs.getString("content");
                    logger.info("Retrieved message from DB - Sender: " + sender + ", Recipient: " + recipient + ", Content: " + content);
                    Message message = new Message(sender, recipient, content);
                    messageList.add(message);
                }
            }
            logger.info("Loaded " + messageList.size() + " messages from the database for user: " + username);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading messages from the database", e);
        }
        this.messages.setAll(messageList);
    }

    public void loadMessageHistory(String recipient) {
        List<Message> messageList = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?)")) {
            String username = client.getUsername();
            stmt.setString(1, username);
            stmt.setString(2, recipient);
            stmt.setString(3, recipient);
            stmt.setString(4, username);

            logger.info("Executing query to load message history between user: " + username + " and recipient: " + recipient);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender_id");
                    String recipientName = rs.getString("recipient_id");
                    String content = rs.getString("content");
                    logger.info("Retrieved message from DB - Sender: " + sender + ", Recipient: " + recipientName + ", Content: " + content);
                    Message message = new Message(sender, recipientName, content);
                    messageList.add(message);
                }
            }
            logger.info("Loaded " + messageList.size() + " messages from the database between user: " + username + " and recipient: " + recipient);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading messages from the database", e);
        }
        this.messages.setAll(messageList);
    }

    public void addNewMessageListener(Consumer<Message> listener) {
        this.newMessageListener = listener;
    }

    private void onNewMessage(Message message) {
        if (newMessageListener != null) {
            Platform.runLater(() -> newMessageListener.accept(message));
        }
    }

    public List<String> getMessageHistory(String username) {
        List<String> messageHistory = null;
        try {
            // Replace this with actual database query logic using DatabaseUtil or your preferred database access method
            messageHistory = DatabaseUtil.loadMessageHistory(username);
            logger.info("Loaded " + messageHistory.size() + " messages from the database for user: " + username);
        } catch (Exception e) {
            logger.severe("Failed to load message history from database: " + e.getMessage());
            e.printStackTrace();
        }
        return messageHistory;
    }

    // Example method to save message
    public void saveMessage(String sender, String recipient, String content) {
        try {
            // Replace this with actual database save logic using DatabaseUtil or your preferred database access method
            DatabaseUtil.saveMessage(sender, recipient, content);
            logger.info("Saved message from " + sender + " to " + recipient + " in the database");
        } catch (Exception e) {
            logger.severe("Failed to save message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
