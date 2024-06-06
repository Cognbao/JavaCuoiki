package org.example.test.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatModel {
    private final ObservableList<String> messages;

    public ChatModel() {
        messages = FXCollections.observableArrayList();
    }

    public ObservableList<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}