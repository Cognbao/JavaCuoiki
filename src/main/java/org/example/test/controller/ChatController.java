package org.example.test.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.test.model.ChatModel;
import org.example.test.network.Client;
import org.example.test.view.ChatView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ChatController implements Initializable {

    @FXML
    public BorderPane root;
    @FXML
    public TextField recipientField;
    @FXML
    public ComboBox recipientComboBox;
    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;
    @FXML
    private ChatView view;

    private ChatModel model;
    private Client client;
    private Stage primaryStage;
    private String currentRecipient;
    private ObservableList<String> messageList = FXCollections.observableArrayList();
    private Runnable onClose;


    public ChatController(){

    }

    public ChatView getView(){
        return view;
    }

    public ChatController(Client client, Stage stage, Runnable onClose) { // Removed showLoginScreen parameter
        this.client = client;
        this.primaryStage = stage;
        this.onClose = onClose;
    }

    @FXML
    public void handleAddFriend() {
        showAddFriendDialog();
    }

    // Method to open the AddFriendView dialog
    private void showAddFriendDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/test/fxml/AddFriendView.fxml"));
            Parent root = loader.load();

            AddFriendController addFriendController = loader.getController();
            addFriendController.setClient(view.getClient());

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setView(ChatView chatView) {
    }

    public void setClient(Client client) {
    }

    public void setPrimaryStage(Stage stage) {
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }
}
