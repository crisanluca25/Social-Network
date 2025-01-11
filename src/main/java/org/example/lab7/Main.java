//package org.example;
//
//
//import domain.Friendship;
//import domain.User;
//import domain.validators.FriendshipValidator;
//import domain.validators.UserValidator;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import repository.FriendshipDBRepository;
//import repository.UserDBRepository;
//import service.SocialNetwork;
//import ui.Console;
//
//
////TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
//// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//public class Main {
//    public static void main(String[] args) {
//        UserDBRepository repoUser = new UserDBRepository(new UserValidator());
//        FriendshipDBRepository repoFriendship = new FriendshipDBRepository(new FriendshipValidator(repoUser));repoUser
//
//
//        SocialNetwork socialNetwork = new SocialNetwork(repoUser, repoFriendship);
//        Console ui = new Console(socialNetwork);
//
//        ui.run();
//
//    }
//}
