package controller;

import domain.FriendRequest;
import domain.User;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.SocialNetwork;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendRequestsWindow {

    private final SocialNetwork socialNetwork;
    private final User currentUser;

    public FriendRequestsWindow(SocialNetwork socialNetwork, User currentUser) {
        this.socialNetwork = socialNetwork;
        this.currentUser = currentUser;
    }

    public void show() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Friend Requests");

        VBox dialogLayout = new VBox(10);
        dialogLayout.getStyleClass().add("friend-requests-root");

        List<FriendRequest> requests = socialNetwork.getPendingRequests(currentUser.getId());

        for (FriendRequest request : requests) {
            VBox requestBox = new VBox(5);
            requestBox.getStyleClass().add("request-row");

            String sentAt = request.getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            Label senderLabel = new Label("Friend request from: " + socialNetwork.getUsernameById(request.getSenderId()) + "\n     Sent at: " + sentAt);
            senderLabel.getStyleClass().add("sender-label");

            Button acceptButton = new Button("Accept");
            acceptButton.getStyleClass().add("accept-button");

            Button rejectButton = new Button("Reject");
            rejectButton.getStyleClass().add("reject-button");

            acceptButton.setOnAction(e -> {
                try {
                    socialNetwork.acceptFriendRequest(request);
                    AlertHelper.showAlert("Success", "Friend request accepted!");
                    dialogStage.close();
                } catch (Exception ex) {
                    AlertHelper.showAlert("Error", ex.getMessage());
                }
            });

            rejectButton.setOnAction(e -> {
                socialNetwork.rejectFriendRequest(request);
                AlertHelper.showAlert("Success", "Friend request rejected.");
                dialogStage.close();
            });

            requestBox.getChildren().addAll(senderLabel, acceptButton, rejectButton);
            dialogLayout.getChildren().add(requestBox);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(dialogLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        Scene dialogScene = new Scene(scrollPane, 400, 300);
        dialogScene.getStylesheets().add(getClass().getResource("/styles/friendRequests.css").toExternalForm()); // Încarcă fișierul CSS
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }
}
