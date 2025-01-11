package controller;

import domain.FriendRequest;
import domain.User;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.SocialNetwork;

import java.time.LocalDateTime;
import java.util.stream.StreamSupport;

public class AddFriendDialog {

    private final SocialNetwork socialNetwork;
    private final User currentUser;

    public AddFriendDialog(SocialNetwork socialNetwork, User currentUser) {
        this.socialNetwork = socialNetwork;
        this.currentUser = currentUser;
    }

    public void show() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Friend");

        VBox dialogLayout = new VBox(10);
        dialogLayout.getStyleClass().add("dialog-root");

        Label label = new Label("Enter the username of the friend:");
        label.getStyleClass().add("label");

        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("text-field");

        Button addButton = new Button("Send Request");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(e -> {
            String username = usernameField.getText();
            if (username.isEmpty()) {
                AlertHelper.showAlert("Error", "Username cannot be empty.");
                return;
            }

            User friend = StreamSupport.stream(socialNetwork.getUsers().spliterator(), false)
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);

            if (friend == null) {
                AlertHelper.showAlert("Error", "User not found.");
                return;
            }

            if (currentUser.getId().equals(friend.getId())) {
                AlertHelper.showAlert("Error", "You cannot send a friend request to yourself.");
                return;
            }

            FriendRequest request = new FriendRequest(currentUser.getId(), friend.getId(), "Pending", LocalDateTime.now());
            try {
                socialNetwork.sendFriendRequest(request);
                AlertHelper.showAlert("Success", "Friend request sent!");
            } catch (Exception ex) {
                AlertHelper.showAlert("Error", ex.getMessage());
            }

            dialogStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(e -> dialogStage.close());

        dialogLayout.getChildren().addAll(label, usernameField, addButton, cancelButton);

        Scene dialogScene = new Scene(dialogLayout, 300, 200);
        dialogScene.getStylesheets().add(getClass().getResource("/styles/addFriendDialog.css").toExternalForm());
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }
}
