package org.example.test.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.test.model.DatabaseUtil;

import java.sql.*;


public class ChatModel {
    private final StringProperty message = new SimpleStringProperty();

    public void addMessage(String newMessage) {
        message.set(newMessage);
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void sendMessage(String username, String message) {
        DatabaseUtil.insertMessage(username, message);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb","root","Bin141005")) {
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT into chat (username,message,current_timestamp) VALUES (?,?,?)")) {
                pstmt.setString(1, username);
                pstmt.setString(2, message);
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Current timestamp
                pstmt.executeUpdate();
                pstmt.executeUpdate();

                conn.commit(); // Commit if successful
                System.out.println("Message saved successfully.");
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                System.err.println("Error saving message: " + e.getMessage() + ", Error code: " + e.getErrorCode());
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }
    private ObservableList<String> messages = FXCollections.observableArrayList();

    public ObservableList<String> getMessages() {
        return messages;
    }
    public void loadMessageHistory() {
        try (ResultSet rs = DatabaseUtil.getMessageHistory()) {
            while (rs.next()) {
                String sender = rs.getString("username");
                String message = rs.getString("message");
                messages.add(sender + ": " + message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
