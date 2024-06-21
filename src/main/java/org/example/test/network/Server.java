package org.example.test.network;

import org.example.test.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private static final List<ClientThread> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(socket);
                clients.add(clientThread);
                new Thread(clientThread).start();
                broadcastUserList(); // Broadcast updated user list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastUserList() {
        List<String> usernames = clients.stream()
                .map(ClientThread::getUsername)
                .toList();
        Message userListMessage = new Message(usernames);
        for (ClientThread client : clients) {
            client.sendMessage(userListMessage);
        }
    }

    private static void forwardMessage(Message message, ClientThread sender) {
        if (message.getRecipient() == null) { // Broadcast to all clients
            for (ClientThread client : clients) {
                client.sendMessage(message);
            }
        } else { // Private message
            for (ClientThread client : clients) {
                if (client != sender && client.getUsername().equals(message.getRecipient())) {
                    client.sendMessage(message);
                    break; // Found the recipient, no need to send to others
                }
            }
        }
    }

    static class ClientThread implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String username;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                Message loginMessage = (Message) in.readObject();
                username = loginMessage.getSender();
                System.out.println(username + " connected");

                while (true) {
                    try {
                        Message message = (Message) in.readObject();
                        System.out.println("Server received: " + message.getContent() + " from " + message.getSender());
                        forwardMessage(message, this);
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Error reading message from client: " + e.getMessage());
                        clients.remove(this);
                        broadcastUserList(); // Broadcast updated user list
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(Message message) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }
    }
}