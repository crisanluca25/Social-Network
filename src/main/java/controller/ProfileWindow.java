package controller;

import domain.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.SocialNetwork;

import java.io.ByteArrayInputStream;

public class ProfileWindow {
    private final User currentUser;
    private final SocialNetwork socialNetwork;

    public ProfileWindow(User currentUser, SocialNetwork socialNetwork){
        this.currentUser = currentUser;
        this.socialNetwork = socialNetwork;
    }

    public void show() {
        Stage profileStage = new Stage();
        profileStage.setTitle("Profile");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("profile-root");

        ImageView profilePicture = new ImageView();

        profilePicture.setFitWidth(100);
        profilePicture.setFitHeight(100);

        if (currentUser.getProfilePicture() != null && currentUser.getProfilePicture().length > 0) {
            Image image = new Image(new ByteArrayInputStream(currentUser.getProfilePicture()));
            profilePicture.setImage(image);
        } else {
            profilePicture.setImage(new Image(
                    getClass().getResource("/images/defaultprofile.png").toExternalForm()
            ));
        }

        Label usernameLabel = new Label("Username: " + currentUser.getUsername());
        usernameLabel.getStyleClass().add("profile-label");

        Label emailLabel = new Label("Email: " + currentUser.getEmail());
        emailLabel.getStyleClass().add("profile-label");

        int friendsCount = socialNetwork.getFriends(currentUser).size();
        Label friendsCountLabel = new Label("Number of Friends: " + friendsCount);
        friendsCountLabel.getStyleClass().add("profile-label");

        Button changeProfilePictureButton = new Button("Change Profile Picture");
        changeProfilePictureButton.getStyleClass().add("button");
        changeProfilePictureButton.setOnAction(e -> {
            socialNetwork.openFileChooser(profilePicture, currentUser.getId());
            User updatedUser = socialNetwork.findUser(currentUser.getId());
            if (updatedUser != null && updatedUser.getProfilePicture() != null
                    && updatedUser.getProfilePicture().length > 0) {
                Image newImage = new Image(new ByteArrayInputStream(updatedUser.getProfilePicture()));
                profilePicture.setImage(newImage);
            }
        });

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> profileStage.close());

        layout.getChildren().addAll(profilePicture,changeProfilePictureButton, usernameLabel, emailLabel, friendsCountLabel, backButton);

        Scene profileScene = new Scene(layout, 300, 400);
        profileScene.getStylesheets().add(getClass().getResource("/styles/profileWindow.css").toExternalForm());
        profileStage.setScene(profileScene);
        profileStage.show();
    }


}
