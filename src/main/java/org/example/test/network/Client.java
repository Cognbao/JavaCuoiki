package org.example.test.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private String serverAddress;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String serverAddress, int port) throws IOException {
        this.serverAddress = serverAddress;
        this.port = port;
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 12345);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while ((message = console.readLine()) != null) {
                client.sendMessage(message);
                System.out.println("Server: " + client.receiveMessage());
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        socket.close();
    }
}