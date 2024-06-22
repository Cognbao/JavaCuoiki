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

    // Add a new message listener
    public static MessageService getMessageService() {
        return messageService;
    }

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
        String sql = "INSERT INTO chat_messages (sender, recipient, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getRecipient());
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
            sql = "SELECT sender, content FROM chat_messages WHERE recipient IS NULL OR sender = ? OR recipient = ?";
        } else {
            sql = "SELECT sender, content FROM chat_messages WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?)";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (user2 == null) {
                stmt.setString(1, user1);
                stmt.setString(2, user1);
            } else {
                stmt.setString(1, user1);
                stmt.setString(2, user2);
                stmt.setString(3, user2);
                stmt.setString(4, user1);
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
}
