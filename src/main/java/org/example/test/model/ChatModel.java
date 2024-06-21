package org.example.test.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatModel {

    private ObservableList<String> messages = FXCollections.observableArrayList();

    public ObservableList<String> getMessages() {
        return messages;
    }

    public void sendMessage(String username, String message) {
        DatabaseUtil.insertMessage(username, message);
        addMessage(username + ": " + message);
    }

    public void loadMessageHistory() {
        messages.clear();
        messages.addAll(DatabaseUtil.getMessageHistory());
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}