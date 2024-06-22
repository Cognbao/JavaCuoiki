package org.example.test.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.example.test.controller.ChatController;
import org.example.test.network.Client;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChatView extends BorderPane implements Initializable {

    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;
    @FXML
    private ComboBox<String> recipientComboBox;
    @FXML
    private Button addFriendButton;

    private Client client;
    private ChatController controller;
    private ObservableList<String> messageList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageListView.setItems(messageList);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    // ... other getter methods ...

    public void updateRecipientList(List<String> newRecipients) {
        recipientComboBox.getItems().clear();
        recipientComboBox.getItems().addAll(newRecipients);
    }

    public String showUsernameDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Username");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter your username:");
        return dialog.showAndWait().orElse(null);
    }

    public void setController(ChatController chatController) {
        this.controller = chatController;
    }

    public boolean showFriendRequestDialog(String requester) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Friend Request");
        alert.setHeaderText(null);
        alert.setContentText(requester + " wants to add you as a friend.");

        ButtonType acceptButton = new ButtonType("Accept");
        ButtonType declineButton = new ButtonType("Decline");

        alert.getButtonTypes().setAll(acceptButton, declineButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == acceptButton; // Return true if accepted
    }

    public void updateMessageList(List<String> messages) {
        messageListView.getItems().clear(); // Clear previous messages
        messageListView.getItems().addAll(messages); // Add new messages
    }

    public ListView<String> getMessageListView() {
        return messageListView;
    }
}
