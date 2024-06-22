package org.example.test.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.test.network.Client;

import java.util.logging.Logger;

public class AddFriendController {
    private static final Logger logger = Logger.getLogger(AddFriendController.class.getName());

    @FXML
    private TextField usernameField;
    @FXML
    private Button addFriendButton;
    private Client client;

    public AddFriendController() {
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleAddFriend(ActionEvent event) {
        String friendUsername = usernameField.getText().trim();
        if (!friendUsername.isEmpty() && !friendUsername.equals(client.getUsername())) { // Prevent adding self as friend
            client.sendFriendRequest(friendUsername);
            showAlert(Alert.AlertType.INFORMATION, "Friend Request Sent", "Friend request sent to " + friendUsername);

            // Close the dialog after sending the request
            Stage stage = (Stage) addFriendButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid username. Please enter a valid username.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
