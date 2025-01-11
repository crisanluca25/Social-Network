package org.example.lab7;

import controller.LoginRegister;
import controller.MainWindow;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import javafx.application.Application;
import javafx.stage.Stage;
import repository.FriendRequestDBRepository;
import repository.FriendshipDBRepository;
import repository.MessageDBRepository;
import repository.UserDBRepository;
import service.SocialNetwork;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SocialNetwork socialNetwork = new SocialNetwork(new UserDBRepository(new UserValidator()), new FriendshipDBRepository(new FriendshipValidator(new UserDBRepository(new UserValidator()))), new FriendRequestDBRepository(), new MessageDBRepository()); // Instanțierea serviciului principal
        UserDBRepository userDBRepository = new UserDBRepository(new UserValidator());
//        userDBRepository.populateSaltsForExistingUsers();
        LoginRegister loginRegister = new LoginRegister(socialNetwork, (loggedInUser) -> {
            try {
                MainWindow mainWindow = new MainWindow(socialNetwork, loggedInUser);
                mainWindow.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        loginRegister.start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}