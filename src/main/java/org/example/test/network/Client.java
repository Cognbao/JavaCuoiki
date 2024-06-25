package org.example.test.network;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.test.controller.ChatController;
import org.example.test.model.Message;
import org.example.test.view.ChatView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.test.model.Message.MessageType;

public class Client implements Runnable {
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static Client instance; // Static instance
    private ChatView view;
    private String hostName;
    private int portNumber;
    private String username;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Consumer<Message> newMessageListener;
    private Thread listenerThread;
    private ChatController chatController;

    private Set<String> friends = new HashSet<>();

    public Client(ChatView view, String hostName, int portNumber, String username) {
        this.view = view;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.username = username;
        instance = this;
    }

    public Client(ChatController chatController, String host, int port, String username) {
        this.chatController = chatController;
        this.hostName = host;
        this.portNumber = port;
        this.username = username;
        instance = this;
    }

    public void setView(ChatView view) {
        this.view = view;
    }

    public static Client getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Client instance not initialized.");
        }
        return instance;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void startListening() {
        listenerThread = new Thread(this);
        listenerThread.start();
    }

    public void addNewMessageListener(Consumer<Message> listener) {
        this.newMessageListener = listener;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(hostName, portNumber)) {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            logger.info("Connected to server at: " + hostName + ":" + portNumber);
            // Send username to server
            out.writeObject(new Message(username, null, null));

            while (true) {
                try {
                    Message message = (Message) in.readObject();
                    handleIncomingMessage(message);
                } catch (IOException | ClassNotFoundException e) {
                    logger.log(Level.SEVERE, "Error reading message from server", e);
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection error", e);
            Platform.runLater(() -> view.getMessageListView().getItems().add("Connection to server lost."));
        }
    }

    private void handleIncomingMessage(Message message) {
        switch (message.getType()) {
            case USER_LIST -> Platform.runLater(() -> {
                List<String> otherUsers = message.getRecipients().stream()
                        .filter(user -> !user.equals(username) && friends.contains(user))
                        .toList();
                view.updateRecipientList(otherUsers);
            });
            case FRIEND_REQUEST -> Platform.runLater(() -> {
                boolean accepted = view.showFriendRequestDialog(message.getSender());
                try {
                    out.writeObject(new Message(
                            MessageType.FRIEND_RESPONSE,
                            username, message.getSender(),
                            accepted ? "ACCEPTED" : "DECLINED"
                    ));
                    if (accepted) {
                        friends.add(message.getSender());
                        view.updateRecipientList(new ArrayList<>(friends));
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error sending friend response", e);
                }
            });
            case FRIEND_RESPONSE -> Platform.runLater(() -> handleFriendResponse(message));
            case GET_HISTORY ->
                    Platform.runLater(() -> view.updateMessageList(message.getMessageHistory()));
            case MESSAGE -> {
                String recipient = message.getRecipient();
                if (recipient == null || recipient.equals(username)) {
                    Platform.runLater(() -> {
                        String formattedMessage = String.format("[%s]: %s", message.getSender(), message.getContent());
                        view.getMessageListView().getItems().add(formattedMessage);
                    });
                }
            }
            default -> logger.warning("Unknown message type: " + message.getType());
        }
    }

    public void sendFriendRequest(String friendUsername) {
        try {
            Message request = new Message(MessageType.FRIEND_REQUEST, username, friendUsername, null);
            out.writeObject(request);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending friend request", e);
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to send friend request."));
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message, String recipient) {
        if (out != null) { // Check if the output stream is open
            try {
                Message msg = new Message(username, recipient, message);
                out.writeObject(msg);
                // If the message is successfully sent, notify the ChatModel
                if (newMessageListener != null) {
                    newMessageListener.accept(msg);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error sending message", e);
                Platform.runLater(() -> view.getMessageListView().getItems().add("Error sending message."));
            }
        } else {
            logger.warning("Cannot send message. Output stream is not open.");
            // You might want to handle this error differently (e.g., reconnect)
        }
    }

    public void disconnect() {
        System.out.println("Disconnecting from server...");
        if (out != null) {
            try {
                out.writeObject(new Message(MessageType.LOGOUT, username, null, null));
                out.flush();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error sending logout message", e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error closing output stream", e);
                }
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing input stream", e);
            }
        }
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }
    }

    private void handleFriendResponse(Message message) {
        String friendUsername = message.getSender(); // The sender of the response is the friend
        String response = message.getContent();

        Platform.runLater(() -> {
            if (response.equals("ACCEPTED")) {
                friends.add(friendUsername);
                view.updateRecipientList(new ArrayList<>(friends));
                showAlert(Alert.AlertType.INFORMATION, "Friend Request Accepted",
                        friendUsername + " accepted your friend request!");
            } else if (response.equals("DECLINED")) {
                showAlert(Alert.AlertType.INFORMATION, "Friend Request Declined",
                        friendUsername + " declined your friend request.");
            } else {
                logger.warning("Invalid friend response: " + response);
                // Optionally, show a generic error message to the user
            }
        });
    }
}
