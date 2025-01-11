package controller;

import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.SocialNetwork;

import java.util.List;

public class FriendsWindow {

    private final SocialNetwork socialNetwork;
    private final User currentUser;

    public FriendsWindow(SocialNetwork socialNetwork, User currentUser) {
        this.socialNetwork = socialNetwork;
        this.currentUser = currentUser;
    }

    public void show() {
        Stage friendsStage = new Stage();
        friendsStage.setTitle("Your Friends");

        VBox layout = new VBox(10);
        layout.getStyleClass().add("friends-window");

        Label titleLabel = new Label("Friends List");
        titleLabel.getStyleClass().add("title-label");
        layout.getChildren().add(titleLabel);


        ListView<User> friendsListView = new ListView<>();
        friendsListView.getStyleClass().add("friends-list");

        int pageSize = 3;
        Pagination pagination = new Pagination();
        pagination.setPageCount((int) Math.ceil((double) socialNetwork.getTotalFriends(currentUser) / pageSize));
        pagination.setPageFactory(pageIndex -> {
            List<User> friends = socialNetwork.getFriendsPaginated(currentUser, pageIndex * pageSize, pageSize);
            friendsListView.setItems(FXCollections.observableList(friends));
            return new VBox(friendsListView);
        });

        friendsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    HBox hbox = new HBox(10);
                    hbox.setStyle("-fx-alignment: center-left;");

                    Label usernameLabel = new Label(item.getUsername());
                    usernameLabel.getStyleClass().add("friend-item");

                    Button deleteButton = new Button("Delete");
                    deleteButton.getStyleClass().add("delete-button");
                    deleteButton.setOnAction(e -> {
                        socialNetwork.removeFriendship(currentUser.getId(), item.getId());
                        friendsListView.getItems().remove(item);
                        AlertHelper.showAlert("Success", "Friend removed.");
                    });

                    Button messageButton = new Button("Message");
                    messageButton.getStyleClass().add("delete-button");
                    messageButton.setOnAction(e -> new ChatWindow(socialNetwork, currentUser, item).show());

                    Button viewProfileButton = new Button("View Profile");
                    viewProfileButton.getStyleClass().add("delete-button");
                    viewProfileButton.setOnAction(e -> new ProfileWindow(item, socialNetwork).show());

                    hbox.getChildren().addAll(usernameLabel, messageButton, viewProfileButton, deleteButton);
                    setGraphic(hbox);
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });

        layout.getChildren().addAll(friendsListView, pagination);

        Scene friendsScene = new Scene(layout, 400, 500);
        friendsScene.getStylesheets().add(getClass().getResource("/styles/friendsWindow.css").toExternalForm());
        friendsStage.setScene(friendsScene);
        friendsStage.show();
    }
}
