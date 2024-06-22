package org.example.test.model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

    // Message attributes
    private MessageType type;
    private String sender;
    private String recipient; // Null for public messages
    private String content;
    // For USER_LIST messages
    private List<String> recipients;
    // For GET_HISTORY and MESSAGE_HISTORY messages
    private List<String> messageHistory; // Store message history for a specific conversation

    // Constructor for regular messages
    public Message(String sender, String recipient, String content) {
        this.type = MessageType.MESSAGE;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    // Constructor for user list messages
    public Message(List<String> recipients) {
        this.type = MessageType.USER_LIST;
        this.recipients = recipients;
    }

    // Constructor for friend requests and responses
    public Message(MessageType type, String sender, String recipient, String content) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    // Constructor for message history request
    public Message(MessageType type, String sender, String recipient) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
    }

    // Constructor for message history response
    public Message(MessageType type, List<String> messageHistory) {
        this.type = type;
        this.messageHistory = messageHistory;
    }

    // Constructor for LOGOUT messages
    public Message(MessageType type, String sender) {
        this.type = type;
        this.sender = sender;
    }

    // Getters and setters
    public MessageType getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public List<String> getMessageHistory() {
        return messageHistory;
    }

    public void setMessageHistory(List<String> messageHistory) {
        this.messageHistory = messageHistory;
    }

    // Define message types
    public enum MessageType {
        USER_LIST,
        MESSAGE,
        FRIEND_REQUEST,
        FRIEND_RESPONSE,
        GET_HISTORY,
        MESSAGE_HISTORY,
        LOGOUT
    }
}
