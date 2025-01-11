package repository;

import domain.FriendRequest;
import domain.validators.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestDBRepository{
    private final String url = "jdbc:postgresql://127.0.0.1:5432/socialnetwork?ssl=false";
    private final String username = "postgres";
    private final String password = "lucacrisan";

    public void save(FriendRequest friendRequest) {
        String sql = "INSERT INTO FriendRequests (sender_id, receiver_id, status, request_time) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, friendRequest.getSenderId());
            statement.setLong(2, friendRequest.getReceiverId());
            statement.setString(3, friendRequest.getStatus());
            statement.setTimestamp(4, Timestamp.valueOf(friendRequest.getSentAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ValidationException("Error while saving friend request: " + e.getMessage());
        }
    }

    public Optional<FriendRequest> findBySenderAndReceiver(Long senderId, Long receiverId) {
        String sql = "SELECT * FROM FriendRequests WHERE sender_id = ? AND receiver_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, senderId);
            statement.setLong(2, receiverId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapToFriendRequest(resultSet));
            }
        } catch (SQLException e) {
            throw new ValidationException("Error while finding friend request: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<FriendRequest> findByReceiver(Long receiverId) {
        String sql = "SELECT * FROM FriendRequests WHERE receiver_id = ?";
        return findByColumn(sql, receiverId);
    }

    public List<FriendRequest> findBySender(Long senderId) {
        String sql = "SELECT * FROM FriendRequests WHERE sender_id = ?";
        return findByColumn(sql, senderId);
    }

    public void update(FriendRequest friendRequest) {
        String sql = "UPDATE FriendRequests SET status = ? WHERE sender_id = ? AND receiver_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, friendRequest.getStatus());
            statement.setLong(2, friendRequest.getSenderId());
            statement.setLong(3, friendRequest.getReceiverId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ValidationException("Error while updating friend request: " + e.getMessage());
        }
    }

    public void delete(Long senderId, Long receiverId) {
        String sql = "DELETE FROM FriendRequests WHERE sender_id = ? AND receiver_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, senderId);
            statement.setLong(2, receiverId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ValidationException("Error while deleting friend request: " + e.getMessage());
        }
    }

    private List<FriendRequest> findByColumn(String sql, Long value) {
        List<FriendRequest> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, value);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                requests.add(mapToFriendRequest(resultSet));
            }
        } catch (SQLException e) {
            throw new ValidationException("Error while retrieving friend requests: " + e.getMessage());
        }
        return requests;
    }

    private FriendRequest mapToFriendRequest(ResultSet resultSet) throws SQLException {
        return new FriendRequest(
                resultSet.getLong("sender_id"),
                resultSet.getLong("receiver_id"),
                resultSet.getString("status"),
                resultSet.getTimestamp("request_time").toLocalDateTime()
        );
    }
}
