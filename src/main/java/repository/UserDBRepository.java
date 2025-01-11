package repository;

import domain.User;
import domain.validators.UserValidator;
import utils.PasswordUtils;
import utils.SaltGenerator;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class UserDBRepository implements Repository<Long, User> {
    UserValidator validator;

    public UserDBRepository(UserValidator validator) {
        this.validator = validator;
    }

    @Override
    public Optional<User> find(Long aLong) {
        String query = "SELECT * FROM users WHERE id = ?";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);){
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String salt = resultSet.getString("salt");
                byte[] profilePicture = resultSet.getBytes("profile_picture");
                user = new User(email, username, password, salt, profilePicture);
                user.setID(aLong);
                user.setSalt(salt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> getAll() {
        HashMap<Long, User> users = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String salt = resultSet.getString("salt");
                byte[] profilePicture = resultSet.getBytes("profile_picture");
                User user = new User(email, username, password, salt, profilePicture);
                user.setID(id);
                user.setSalt(salt);
                users.put(user.getId(), user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users.values();
    }

    @Override
    public Optional<User> save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null!");
        }

        String query = "INSERT INTO users(id, email, username, password, salt, profilePicture) VALUES (?,?,?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, user.getId());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getSalt());
            statement.setBytes(6, user.getProfilePicture());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(user);
    }

    @Override
    public Optional<User> delete(Long aLong) {
        Optional<User> userToDelete = find(aLong);
        if (userToDelete.isEmpty()) {
            return Optional.empty();
        }
        String query = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userToDelete;
    }

    public void populateSaltsForExistingUsers() {
        String selectQuery = "SELECT id, password FROM users WHERE salt IS NULL";
        String updateQuery = "UPDATE users SET salt = ?, password = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
             ResultSet resultSet = selectStmt.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String existingPassword = resultSet.getString("password");

                String salt = SaltGenerator.generateSalt();
                String hashedPassword = PasswordUtils.hashPassword(existingPassword, salt);

                updateStmt.setString(1, salt);
                updateStmt.setString(2, hashedPassword);
                updateStmt.setLong(3, id);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProfilePicture(Long userId, File imageFile) {
        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String query = "UPDATE users SET profile_picture = ? WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setBytes(1, imageBytes);
                statement.setLong(2, userId);
                statement.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Error updating profile picture", e);
        }
    }

}
