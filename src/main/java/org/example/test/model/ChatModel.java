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

    public void sendMessage(String username, String messageText) {
        message.set(messageText);
        DatabaseUtil.insertMessage(username, messageText);
    }
    private ObservableList<String> messages = FXCollections.observableArrayList();

    public ObservableList<String> getMessages() {
        return messages;
    }
    public void loadMessageHistory() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/UserDB", "root", "Bin141005")) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM chat_messages");

            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");
                messages.add(sender + ": " + message); // Add to ObservableList
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
