package org.example.test.network;

import org.example.test.model.DatabaseUtil;
import org.example.test.model.Message;
import org.example.test.service.MessageService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.example.test.model.Message.MessageType.FRIEND_RESPONSE;

public class Server {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final Map<String, List<String>> friendships = new HashMap<>();
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static MessageService messageService;

    public static MessageService getMessageService() {
        return messageService;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server is running on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                synchronized (clients) {
                    clients.add(clientHandler);
                }
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error accepting client connection", e);
        }
    }

    public static void broadcastUserList() {
        List<String> usernames = clients.stream()
                .map(ClientHandler::getUsername)
                .collect(Collectors.toList());
        Message userListMessage = new Message(usernames);
        for (ClientHandler client : clients) {
            client.sendMessage(userListMessage);
        }
    }

    public static void forwardMessage(Message message, ClientHandler sender) {
        switch (message.getType()) {
            case FRIEND_REQUEST -> {
                String friendUsername = message.getRecipient();
                ClientHandler friendHandler = getClientHandlerByUsername(friendUsername);

                if (friendHandler != null) {
                    friendHandler.sendMessage(message);
                } else {
                    sender.sendMessage(new Message(FRIEND_RESPONSE, "Server", sender.getUsername(), "DECLINED"));
                }
            }
            case FRIEND_RESPONSE -> {
                ClientHandler originalSender = getClientHandlerByUsername(message.getRecipient());
                if (originalSender != null) {
                    originalSender.sendMessage(message);
                    if (message.getContent().equals("ACCEPTED")) {
                        addFriendship(message.getSender(), message.getRecipient());
                    }
                }
            }
            case GET_HISTORY -> {
                List<String> history = messageService.getMessageHistory(
                        message.getSender(), message.getRecipient());
                sender.sendMessage(new Message(Message.MessageType.MESSAGE_HISTORY, history));
            }
            case MESSAGE -> {
                if (message.getRecipient() == null) {
                    // Broadcast to all clients
                    synchronized (clients) {
                        for (ClientHandler client : clients) {
                            if (client != sender) { // Exclude the sender from broadcast
                                client.sendMessage(message);
                            }
                        }
                    }
                    // Save the message to the database
                    try {
                        messageService.saveMessage(message);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Private message
                    synchronized (clients) {
                        for (ClientHandler client : clients) {
                            if (client.getUsername().equals(message.getRecipient())) {
                                client.sendMessage(message);
                                // Save the message to the database
                                try {
                                    messageService.saveMessage(message);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    // ... other methods ...
    public static List<ClientHandler> getClients() {
        synchronized (clients) { // Ensure thread-safety when accessing the shared list
            return new ArrayList<>(clients); // Return a copy of the list
        }
    }

    // Add friend in server
    public static void addFriendship(String user1, String user2) {
        friendships.computeIfAbsent(user1, k -> new ArrayList<>()).add(user2);
        friendships.computeIfAbsent(user2, k -> new ArrayList<>()).add(user1);
        broadcastUserList(); // Update user lists for all clients
    }

    public static boolean areFriends(String user1, String user2) {
        if (user1 == null || user2 == null) {
            return false; // Handle null input
        }

        String sql = "SELECT * FROM friendships WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If a row is found, they are friends
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking friendship in database", e);
            // You could potentially return false or throw an exception here,
            // depending on how you want to handle database errors
            return false;
        }
    }

    public static ClientHandler getClientHandlerByUsername(String username) {
        synchronized (clients) {
            return clients.stream()
                    .filter(client -> client.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
        }
    }
}
