package controller;

import domain.User;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import service.SocialNetwork;
import utils.PasswordUtils;

import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class LoginRegister extends Application {

    private final SocialNetwork socialNetwork;
    private final Consumer<User> onLoginSuccess;
    private Stage loginStage;

    public LoginRegister(SocialNetwork socialNetwork, Consumer<User> onLoginSuccess) {
        this.socialNetwork = socialNetwork;
        this.onLoginSuccess = onLoginSuccess;
    }

    @Override
    public void start(Stage primaryStage) {
        loginStage = primaryStage;
        loginStage.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label loginLabel = new Label("Login");
        loginLabel.getStyleClass().add("title-label");
        GridPane.setHalignment(loginLabel, HPos.CENTER);
        grid.add(loginLabel, 0, 0);

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("btn-primary");
        grid.add(loginButton, 1, 3);

        Hyperlink signUpLink = new Hyperlink("No account? Sign up!");
        grid.add(signUpLink, 1, 4);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                AlertHelper.showAlert("Login Failed", "Both username and password must be filled in.");
            } else {
                User user = StreamSupport.stream(socialNetwork.getUsers().spliterator(), false)
                        .filter(u -> u.getUsername().equals(username))
                        .findFirst()
                        .orElse(null);
                if (user != null && PasswordUtils.verifyPassword(password, user.getPassword(), user.getSalt()))
                {
                    onLoginSuccess.accept(user);
                    loginStage.close();
                } else {
                    AlertHelper.showAlert("Login Failed", "Invalid username or password.");
                }
            }
        });

        signUpLink.setOnAction(e -> showRegisterForm());

        Scene scene = new Scene(grid, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/styles/loginRegister.css").toExternalForm());  // Apply the external CSS file
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRegisterForm() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register");

        GridPane registerGrid = new GridPane();
        registerGrid.setHgap(10);
        registerGrid.setVgap(10);

        Label registerLabel = new Label("Register");
        registerLabel.getStyleClass().add("title-label");
        GridPane.setHalignment(registerLabel, HPos.CENTER);
        registerGrid.add(registerLabel, 0, 0);

        Label emailLabel = new Label("Email:");
        registerGrid.add(emailLabel, 0, 1);
        TextField emailField = new TextField();
        registerGrid.add(emailField, 1, 1);

        Label regUsernameLabel = new Label("Username:");
        registerGrid.add(regUsernameLabel, 0, 2);
        TextField regUsernameField = new TextField();
        registerGrid.add(regUsernameField, 1, 2);

        Label regPasswordLabel = new Label("Password:");
        registerGrid.add(regPasswordLabel, 0, 3);
        PasswordField regPasswordField = new PasswordField();
        registerGrid.add(regPasswordField, 1, 3);

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("btn-primary");
        registerGrid.add(registerButton, 1, 4);

        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String username = regUsernameField.getText();
            String password = regPasswordField.getText();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                AlertHelper.showAlert("Registration Failed", "All fields must be filled in.");
            } else {
                boolean userExists = StreamSupport.stream(socialNetwork.getUsers().spliterator(), false)
                        .anyMatch(u -> u.getUsername().equals(username));

                if (userExists) {
                    AlertHelper.showAlert("Registration Failed", "Username already exists.");
                } else {
                    byte[] defaultProfilePicture = socialNetwork.getDefaultProfilePicture();
                    String salt = PasswordUtils.generateSalt();
                    String hashedPassword = PasswordUtils.hashPassword(password, salt);
                    User newUser = new User(email, username, hashedPassword, salt, defaultProfilePicture);
                    socialNetwork.addUser(newUser);
                    emailField.clear();
                    regUsernameField.clear();
                    regPasswordField.clear();
                    registerStage.close();
                    loginStage.show();
                }
            }
        });

        registerStage.setOnCloseRequest(event -> loginStage.show());

        Scene registerScene = new Scene(registerGrid, 400, 400);
        registerScene.getStylesheets().add(getClass().getResource("/styles/loginRegister.css").toExternalForm());  // Apply the external CSS file
        registerStage.setScene(registerScene);
        registerStage.show();
        loginStage.hide();
    }
}
