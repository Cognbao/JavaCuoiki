package org.example.test.network;

import javafx.application.Platform;
import org.example.test.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.example.test.model.Message.MessageType.FRIEND_RESPONSE;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Receive initial login message
            Message loginMessage = (Message) in.readObject();
            username = loginMessage.getSender();
            logger.info(username + " connected");

            //Send initial list of connected users
            Server.broadcastUserList();

            while (true) {
                try {
                    Message message = (Message) in.readObject();
                    logger.info("Received message from " + username + ": " + message.getContent());

                    switch (message.getType()) {
                        case USER_LIST -> Platform.runLater(() -> {
                            List<String> otherUsers = message.getRecipients().stream()
                                    .filter(user -> !user.equals(username) && Server.areFriends(username, user))
                                    .collect(Collectors.toList());
                            sendMessage(new Message(Message.MessageType.USER_LIST, otherUsers)); // Send only friends
                        });
                        case FRIEND_REQUEST -> {
                            String friendUsername = message.getRecipient();
                            ClientHandler friendHandler = Server.getClientHandlerByUsername(friendUsername);

                            if (friendHandler != null) {
                                // Forward the request to the friend
                                friendHandler.sendMessage(message);
                            } else {
                                // Friend not found, send an error back to the sender
                                sendMessage(new Message(FRIEND_RESPONSE, "Server", username, "DECLINED"));
                            }
                        }
                        case FRIEND_RESPONSE -> {
                            ClientHandler originalSender = Server.getClientHandlerByUsername(message.getRecipient());
                            if (originalSender != null) {
                                originalSender.sendMessage(message);
                                if (message.getContent().equals("ACCEPTED")) {
                                    Server.addFriendship(message.getSender(), message.getRecipient());
                                }
                            }
                        }
                        case GET_HISTORY -> {
                            List<String> history = Server.getMessageService().getMessageHistory(
                                    message.getSender(), message.getRecipient());
                            sendMessage(new Message(Message.MessageType.MESSAGE_HISTORY, history));
                        }
                        case MESSAGE -> Server.forwardMessage(message, this);
                        case LOGOUT -> {
                            logger.info(username + " disconnected");
                            List<ClientHandler> clients = Server.getClients();  // Access the list using the getter
                            synchronized (clients) {
                                clients.remove(this);
                                Server.broadcastUserList(); // Update user list for other clients
                            }
                            return;
                        }
                        default -> logger.warning("Unknown message type: " + message.getType());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    logger.log(Level.SEVERE, "Error reading message from client: " + username, e);
                    List<ClientHandler> clients = Server.getClients();  // Access the list using the getter
                    synchronized (clients) {
                        clients.remove(this);
                        Server.broadcastUserList(); // Update user list for other clients
                    }
                    break; // Exit the loop on error
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error in client handler for: " + username, e);
        } finally {
            try {
                if (socket != null) socket.close();
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing resources for client: " + username, e);
            }
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending message to client: " + username, e);
        }
    }

    public String getUsername() {
        return username;
    }
}

