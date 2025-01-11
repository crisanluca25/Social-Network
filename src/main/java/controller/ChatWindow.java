package controller;

import domain.Message;
import domain.User;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.SocialNetwork;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ChatWindow {

    private final SocialNetwork socialNetwork;
    private final User currentUser;
    private final User friend;

    public ChatWindow(SocialNetwork socialNetwork, User currentUser, User friend) {
        this.socialNetwork = socialNetwork;
        this.currentUser = currentUser;
        this.friend = friend;
    }

    public void show() {
        Stage messageStage = new Stage();
        messageStage.setTitle("Messages with " + friend.getUsername());

        ListView<String> messageList = new ListView<>();
        messageList.setPrefHeight(300);

        loadMessages(messageList);

        TextField messageInput = new TextField();
        messageInput.setPromptText("Type your message...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage(messageInput, messageList));

        VBox layout = new VBox(10, messageList, messageInput, sendButton);

        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/styles/chatwindow.css").toExternalForm());
        messageStage.setScene(scene);
        messageStage.show();
    }

    private void loadMessages(ListView<String> messageList) {

        List<Message> messages = socialNetwork.getMessages(currentUser, friend);

        for (Message message : messages) {
            String senderUsername = message.getSender().getEmail();

            String messageText = message.getText();

            messageList.getItems().add(senderUsername + ": " + messageText);
        }
    }



    private void sendMessage(TextField messageInput, ListView<String> messageList) {
        String messageText = messageInput.getText();
        if (!messageText.isEmpty()) {
            Message message = new Message(currentUser, friend, messageText, LocalDateTime.now());
            socialNetwork.saveMessage(message);
            messageList.getItems().add(currentUser.getUsername() + ": " + messageText);
            messageInput.clear();
        }
    }
}
