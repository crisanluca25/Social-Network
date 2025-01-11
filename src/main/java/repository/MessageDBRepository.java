package repository;

import domain.Message;
import domain.User;
import domain.validators.ValidationException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDBRepository {
    private final String url = "jdbc:postgresql://127.0.0.1:5432/socialnetwork?ssl=false";
    private final String username = "postgres";
    private final String password = "lucacrisan";

    public void save(Message message) {
        String sql = "INSERT INTO Messages (sender_id, receiver_id, text, sent_at) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, message.getSender().getId());
            statement.setLong(2, message.getReceiver().getId());
            statement.setString(3, message.getText());
            statement.setTimestamp(4, Timestamp.valueOf(message.getSentAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ValidationException("Error while saving message: " + e.getMessage());
        }
    }

    public List<Message> getMessages(User user1, User user2) {
        String sql = "SELECT * FROM Messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        List<Message> messages = new ArrayList<>();
        Map<Long, User> usersMap = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, user1.getId());
            statement.setLong(2, user2.getId());
            statement.setLong(3, user2.getId());
            statement.setLong(4, user1.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long senderId = resultSet.getLong("sender_id");
                    long receiverId = resultSet.getLong("receiver_id");
                    String messageText = resultSet.getString("text");
                    LocalDateTime sentAt = resultSet.getTimestamp("sent_at").toLocalDateTime();

                    if (!usersMap.containsKey(senderId)) {
                        usersMap.put(senderId, getUserById(senderId));
                    }
                    if (!usersMap.containsKey(receiverId)) {
                        usersMap.put(receiverId, getUserById(receiverId));
                    }

                    User sender = usersMap.get(senderId);
                    User receiver = usersMap.get(receiverId);

                    Message message = new Message(sender, receiver, messageText, sentAt);
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            throw new ValidationException("Error while retrieving messages: " + e.getMessage());
        }

        return messages;
    }


    public User getUserById(long userId) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    String salt = resultSet.getString("salt");
                    byte[] profilePicture = resultSet.getBytes("profile_picture");
                    return new User(username, email, password, salt, profilePicture);
                } else {
                    throw new SQLException("User not found");
                }
            }
        } catch (SQLException e) {
            throw new ValidationException("Error while retrieving user: " + e.getMessage());
        }
    }



}