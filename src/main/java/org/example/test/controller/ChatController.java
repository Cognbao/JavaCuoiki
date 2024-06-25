package org.example.test.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.model.ChatModel;
import org.example.test.model.Message;
import org.example.test.network.Client;
import org.example.test.view.ChatView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatController implements Initializable {
    private static final Logger logger = Logger.getLogger(ChatController.class.getName());
    private ChatView view;

    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField recipientField;
    @FXML
    private ComboBox<String> recipientComboBox;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;
    @FXML
    private Button addFriendButton;

    private Client client;
    private ChatModel model;
    private String currentRecipient; // Keep track of the currently selected recipient
    private ObservableList<String> messageList = FXCollections.observableArrayList(); // Observable list for messages

    public ChatController() {
    }

    public void setModel(ChatModel model) {
        this.model = model;
        try {
            initializeModel();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing model", e);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("ChatController initialize");
        // Initialize UI components and event handlers
        try {
            sendButton.setOnAction(event -> sendMessage());
            addFriendButton.setOnAction(event -> showAddFriendDialog());
            recipientComboBox.setOnAction(this::handleRecipientChange);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during initialization", e);
        }
    }

    private void initializeModel() {
        logger.info("Initializing model in ChatController");
        if (client == null || client.getUsername() == null) {
            logger.severe("Client or username is null, cannot load message history.");
            return;
        }
        try {
            model.loadMessageHistory(client.getUsername());
            logger.info("Messages loaded successfully.");
            List<String> messages = model.getMessageHistory(client.getUsername());
            if (messages != null) {
                messageListView.getItems().setAll(messages);
            }
        } catch (Exception e) {
            logger.severe("Error initializing model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateMessageList(ObservableList<Message> messages) {
        messageList.clear();
        for (Message message : messages) {
            messageList.add(formatMessage(message));
        }
        messageListView.setItems(messageList);
    }

    private String formatMessage(Message message) {
        return String.format("[%s]: %s", message.getSender(), message.getContent());
    }

    private void handleRecipientChange(ActionEvent event) {
        try {
            currentRecipient = recipientComboBox.getValue();
            model.loadMessageHistory(currentRecipient); // Reload chat history for the selected recipient
            updateMessageList(model.getMessages());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error handling recipient change", e);
        }
    }

    public void appendMessage(Message message) {
        Platform.runLater(() -> {
            try {
                if (message.getRecipient() == null || message.getRecipient().equals(client.getUsername())) {
                    String formattedMessage = formatMessage(message);
                    messageList.add(formattedMessage);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error appending message", e);
            }
        });
    }

    private void showAddFriendDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/AddFriendView.fxml"));
            Parent root = loader.load();

            AddFriendController addFriendController = loader.getController();
            addFriendController.setClient(client);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error showing Add Friend dialog", e);
        }
    }

    private void sendMessage() {
        try {
            String messageText = inputField.getText();
            if (!messageText.isEmpty()) {
                model.sendMessage(client.getUsername(), messageText);
                logger.info("Message sent to model.");
                inputField.clear();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending message", e);
        }
    }

    public ChatView getView() {
        return view;
    }

    public void setView(ChatView view) {
        this.view = view;
    }
}
