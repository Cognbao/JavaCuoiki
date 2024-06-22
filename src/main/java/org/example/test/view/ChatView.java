package org.example.test.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.example.test.controller.ChatController;
import org.example.test.network.Client;

import java.net.URL;
import java.util.List;
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
    private ObservableList<String> recipients;
    private ChatController controller;
    private Button getAddFriendButton;

    public ChatView() {
        messageListView = new ListView<>();
        inputField = new TextField();
        sendButton = new Button("Send");

        recipients = FXCollections.observableArrayList();
        recipientComboBox = new ComboBox<>(recipients);
        recipientComboBox.setPromptText("Chọn người nhận");

        HBox inputBox = new HBox(10, recipientComboBox, inputField, sendButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));

        setCenter(messageListView);
        setBottom(inputBox);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recipients = FXCollections.observableArrayList();
        recipientComboBox.setItems(recipients);

    }

    public ListView<String> getMessageListView() {
        return messageListView;
    }

    public TextField getInputField() {
        return inputField;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public ComboBox<String> getRecipientComboBox() {
        return recipientComboBox;
    }

    public void updateRecipientList(List<String> newRecipients) {
        recipients.setAll(newRecipients);
    }

    public String getSelectedRecipient() {
        return recipientComboBox.getValue();
    }

    public String showUsernameDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Username");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter your username:");
        return dialog.showAndWait().orElse(null);
    }

    public void setController(ChatController controller) {
        this.controller = controller;
    }

    public Button getAddFriendButton() {
        return addFriendButton;
    }

    public void updateMessageList(List<String> messages) {
        messageListView.getItems().addAll(messages);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean showFriendRequestDialog(String sender) {

    }
}