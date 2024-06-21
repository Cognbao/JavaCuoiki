package org.example.test.network;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.test.model.ChatModel;
import org.example.test.model.Message;

import java.io.*;
import java.net.Socket;

public class Client extends Application implements Runnable {

    private String hostName = "localhost";
    private int portNumber = 12345;
    private String username;
    private org.example.test.view.ChatView view;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BufferedReader console;
    private Thread listenerThread;

    public Client(String localhost, int i) {

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/ChatView.fxml")); // Path to FXML
            Parent root = loader.load();
            view = loader.getController(); // Get the ChatView controller from FXML
            view.getSendButton().setOnAction(event -> sendMessage(view.getInputField().getText()));

            Scene scene = new Scene(root, 400, 400);
            primaryStage.setScene(scene);
            username = view.showUsernameDialog();
            if (username == null || username.trim().isEmpty()) {
                System.err.println("Invalid username. Exiting.");
                Platform.exit();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        listenerThread = new Thread(this);
        listenerThread.start();

        console = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                String message = console.readLine();
                if (message != null) {
                    sendMessage(message); // Provide the 'message' String to the method
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        String recipient = view.getSelectedRecipient();
        if (recipient != null && !message.isEmpty()) {
            Message msg = new Message(username, recipient, message);
            try {
                out.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(hostName, portNumber)) {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Send username to server
            out.writeObject(new Message(username, null, null));

            while (true) {
                try {
                    Message message = (Message) in.readObject();
                    if (message.getType() == Message.MessageType.USER_LIST) {
                        Platform.runLater(() -> view.updateRecipientList(message.getRecipients()));
                    } else { // This handles both public and private messages
                        if (message.getRecipient() == null || message.getRecipient().equals(username)) {
                            // This is a public message or a private message for this client
                            String formattedMessage = String.format("[%s]: %s", message.getSender(), message.getContent());
                            Platform.runLater(() -> view.getMessageListView().getItems().add(formattedMessage));
                        } // Ignore private messages for other clients
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                view.getMessageListView().getItems().add("Connection to server lost.");
            });
        }
    }

    @Override
    public void stop() throws Exception {
        if (out != null) out.close();
        if (in != null) in.close();
        if (console != null) console.close();
    }

    public void startListening(ChatModel model) {

    }
}