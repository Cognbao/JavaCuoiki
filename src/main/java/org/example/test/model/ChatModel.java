package org.example.test.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.test.model.DatabaseUtil;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ChatModel {
    private final StringProperty message = new SimpleStringProperty();

    public void addMessage(String newMessage) {
        message.set(newMessage);
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void sendMessage(String username, String messageText) {
        message.set(messageText);
        DatabaseUtil.insertMessage(username, messageText);
    }

    public void loadMessageHistory() {
        ResultSet rs = DatabaseUtil.getMessageHistory();
        try {
            while (rs != null && rs.next()) {
                String username = rs.getString("username");
                String message = rs.getString("message");
                String timestamp = rs.getString("timestamp");
                // Handle loading the message into the chat history
                // This will depend on how you want to display it
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
