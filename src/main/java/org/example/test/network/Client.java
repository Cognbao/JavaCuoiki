package org.example.test.network;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.test.model.ChatModel;
import org.example.test.model.Message;
import org.example.test.view.ChatView;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.example.test.model.Message.MessageType;

public class Client extends Application implements Runnable {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private String hostName = "localhost";
    private int portNumber = 12345;
    private String username;
    private org.example.test.view.ChatView view;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BufferedReader console;
    private Thread listenerThread;
    private Set<String> friends = new HashSet<>();

    public Client(String localhost, int i) {

    }

    public Client(ChatView view, String hostName, int portNumber, String username) {
        this.view = view;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.username = username;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ChatView getView() {
        return view;
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

                    if (message.getType() == MessageType.USER_LIST) {
                        Platform.runLater(() -> {
                            List<String> otherUsers = message.getRecipients().stream()
                                    .filter(user -> !user.equals(username) && friends.contains(user))
                                    .toList();
                            view.updateRecipientList(otherUsers);
                        });
                    } else if (message.getType() == MessageType.FRIEND_REQUEST) {
                        Platform.runLater(() -> {
                            boolean accepted = view.showFriendRequestDialog(message.getSender());
                            try {
                                out.writeObject(new Message(
                                        MessageType.FRIEND_RESPONSE,
                                        username, message.getSender(),
                                        accepted ? "ACCEPTED" : "DECLINED"
                                ));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (message.getType() == MessageType.FRIEND_RESPONSE) {
                        Platform.runLater(() -> {
                            handleFriendResponse(message);
                        });
                    } else if (message.getType() == MessageType.GET_HISTORY) {
                        Platform.runLater(() -> view.updateMessageList(message.getMessageHistory())); // Update chat history
                    } else { // Regular message
                        String recipient = message.getRecipient();
                        if (recipient == null || recipient.equals(username)) {
                            Platform.runLater(() -> {
                                String formattedMessage = String.format("[%s]: %s", message.getSender(), message.getContent());
                                view.getMessageListView().getItems().add(formattedMessage);
                            });
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break; // Exit the loop on error
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                view.getMessageListView().getItems().add("Connection to server lost.");
            });
        }
    }

    private void handleFriendResponse(Message message) {
        String friendUsername = message.getSender(); // The sender of the response is the friend
        String response = message.getContent();

        Platform.runLater(() -> {
            if (response.equals("ACCEPTED")) {
                friends.add(friendUsername);
                view.updateRecipientList(new ArrayList<>(friends)); // Refresh recipient list
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

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Optionally, you can set a header text if needed
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        if (out != null) out.close();
        if (in != null) in.close();
        if (console != null) console.close();
    }

    public void startListening(ChatModel model) {

    }

    public void startListening() {
        listenerThread = new Thread(this);
        listenerThread.start();
    }

    public String getUsername() {

        return username;
    }

    public void requestMessageHistory(String recipient) {
        try {
            Message historyRequest = new Message(MessageType.GET_HISTORY, username, recipient); // update
            out.writeObject(historyRequest);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }

    public void disconnect() throws IOException {
        if (out != null) {
            out.writeObject(new Message(MessageType.LOGOUT, username, null, null));
            out.flush();
            out.close();
        }
        if (in != null) in.close();
        listenerThread.interrupt();
    }
}