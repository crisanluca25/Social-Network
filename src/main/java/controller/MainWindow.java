package controller;

import domain.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.SocialNetwork;

public class MainWindow extends Application {

    private final SocialNetwork socialNetwork;
    private final User currentUser;

    public MainWindow(SocialNetwork socialNetwork, User currentUser) {
        this.socialNetwork = socialNetwork;
        this.currentUser = currentUser;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Social Network");

        Label titleLabel = new Label("Welcome " + currentUser.getUsername() + " !");
        titleLabel.getStyleClass().add("title-label");

        VBox layout = new VBox(10);
        layout.getStyleClass().add("root");

        Button profileButton = new Button("My profile");
        profileButton.getStyleClass().add("button");
        profileButton.setOnAction(e -> new ProfileWindow(currentUser, socialNetwork).show());


        Button friendsButton = new Button("Friends");
        friendsButton.getStyleClass().add("button");
        friendsButton.setOnAction(e -> new FriendsWindow(socialNetwork, currentUser).show());

        Button addFriendButton = new Button("Add Friend");
        addFriendButton.getStyleClass().add("button");
        addFriendButton.setOnAction(e -> new AddFriendDialog(socialNetwork, currentUser).show());

        Button friendRequestsButton = new Button("Friend Requests");
        friendRequestsButton.getStyleClass().add("button");
        friendRequestsButton.setOnAction(e -> new FriendRequestsWindow(socialNetwork, currentUser).show());

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("button");
        logoutButton.setOnAction(e -> {
            primaryStage.close();
            new LoginRegister(socialNetwork, user -> {
                new MainWindow(socialNetwork, user).start(new Stage());
            }).start(new Stage());
        });

        Button deleteAccountButton = new Button("Delete Account");
        deleteAccountButton.getStyleClass().add("button");
        deleteAccountButton.setOnAction(e -> {
            boolean confirmed = AlertHelper.showConfirmation("Delete Account",
                    "Are you sure you want to delete your account? This action cannot be undone.");
            if (confirmed) {
                socialNetwork.removeUser(currentUser.getId());
                primaryStage.close();
                new LoginRegister(socialNetwork, user -> {
                    new MainWindow(socialNetwork, user).start(new Stage());
                }).start(new Stage());
            }
        });

        layout.getChildren().addAll(titleLabel, profileButton, friendsButton, addFriendButton, friendRequestsButton, logoutButton, deleteAccountButton);

        Scene mainScene = new Scene(layout, 400, 300);
        mainScene.getStylesheets().add(getClass().getResource("/styles/mainWindow.css").toExternalForm());
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
