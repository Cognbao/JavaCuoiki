package org.example.test.service;

import org.example.test.model.DatabaseUtil;
import org.example.test.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageService {
    private static final Logger logger = Logger.getLogger(MessageService.class.getName());
    private static MessageService messageService; // Keep it private
    private List<Consumer<Message>> newMessageListeners = new CopyOnWriteArrayList<>(); // Thread-safe list of listeners

    // Singleton pattern to get the MessageService instance
    public static MessageService getMessageService() {
        if (messageService == null) {
            messageService = new MessageService();
        }
        return messageService;
    }

    // Add a new message listener
    public void registerNewMessageListener(Consumer<Message> listener) {
        newMessageListeners.add(listener);
    }

    // Call all registered listeners when a new message is received
    private void notifyNewMessage(Message message) {
        for (Consumer<Message> listener : newMessageListeners) {
            listener.accept(message);
        }
    }

    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO Messages (sender_id, recipient_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getUserIdByUsername(message.getSender()));
            stmt.setInt(2, getUserIdByUsername(message.getRecipient()));
            stmt.setString(3, message.getContent());

            stmt.executeUpdate();
            logger.info("Message saved to database: " + message.getContent());
            notifyNewMessage(message);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving message to database: ", e);
            throw e;
        }
    }

    public List<String> getMessageHistory(String user1, String user2) {
        // Retrieve message history for a conversation between user1 and user2 (or all messages if user2 is null)
        List<String> messages = new ArrayList<>();
        String sql;

        if (user2 == null) {
            sql = "SELECT u.username as sender, m.content FROM Messages m " +
                    "JOIN Users u ON m.sender_id = u.user_id " +
                    "WHERE m.recipient_id IS NULL OR m.sender_id = ? OR m.recipient_id = ?";
        } else {
            sql = "SELECT u.username as sender, m.content FROM Messages m " +
                    "JOIN Users u ON m.sender_id = u.user_id " +
                    "WHERE (m.sender_id = ? AND m.recipient_id = ?) OR (m.sender_id = ? AND m.recipient_id = ?)";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (user2 == null) {
                int userId = getUserIdByUsername(user1);
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
            } else {
                int userId1 = getUserIdByUsername(user1);
                int userId2 = getUserIdByUsername(user2);
                stmt.setInt(1, userId1);
                stmt.setInt(2, userId2);
                stmt.setInt(3, userId2);
                stmt.setInt(4, userId1);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String content = rs.getString("content");
                    messages.add("[" + sender + "]: " + content);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving message history", e);
        }
        return messages;
    }

    public List<String> getMessageHistory(String user1) {
        // Call the two-argument version with user2 = null to get all messages for user1
        return getMessageHistory(user1, null);
    }

    private int getUserIdByUsername(String username) throws SQLException {
        String query = "SELECT user_id FROM Users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    throw new SQLException("User not found: " + username);
                }
            }
        }
    }
}
