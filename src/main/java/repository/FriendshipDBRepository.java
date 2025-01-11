package repository;

import domain.Friendship;
import domain.validators.FriendshipValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.time.ZoneOffset;

public class FriendshipDBRepository implements Repository<Long, Friendship> {

    FriendshipValidator friendshipValidator;

    public FriendshipDBRepository(FriendshipValidator friendshipValidator) {
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public Optional<Friendship> find(Long aLong) {
        String query = "SELECT * FROM friendships WHERE id = ?";
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idFriend1 = resultSet.getLong("id_user1");
                Long idFriend2 = resultSet.getLong("id_user2");
                Timestamp date = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsFrom = date.toLocalDateTime();
                friendship = new Friendship(idFriend1, idFriend2, friendsFrom);
                friendship.setID(aLong);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendship);
    }

    @Override
    public Iterable<Friendship> getAll() {
        Map<Long, Friendship> friendships = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("id_user1");
                Long idFriend2 = resultSet.getLong("id_user2");
                Timestamp date = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsFrom = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(0));
                Friendship friendship = new Friendship(idFriend1, idFriend2, friendsFrom);
                friendship.setID(id);
                friendships.put(friendship.getId(), friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships.values();
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        String query = "INSERT INTO friendships(id, id_user1, id_user2, friendsFrom) VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getIdUser1());
            statement.setLong(3, entity.getIdUser2());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(entity.getFriendsFrom()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Long aLong) {
        String query = "DELETE FROM friendships WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Friendship friendshipToDelete = null;
        for (Friendship friendship : getAll()) {
            if (Objects.equals(friendship.getId(), aLong)) {
                friendshipToDelete = friendship;
            }
        }
        return Optional.ofNullable(friendshipToDelete);
    }

    public List<Friendship> findByUser(Long userId) {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships WHERE id_user1 = ? OR id_user2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idUser1 = resultSet.getLong("id_user1");
                Long idUser2 = resultSet.getLong("id_user2");
                Timestamp date = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsFrom = date.toLocalDateTime();
                Friendship friendship = new Friendship(idUser1, idUser2, friendsFrom);
                friendship.setID(id);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }

    public List<Friendship> findByUserPaginated(Long userId, int offset, int limit) {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships WHERE id_user1 = ? OR id_user2 = ? LIMIT ? OFFSET ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            statement.setInt(3, limit);
            statement.setInt(4, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idUser1 = resultSet.getLong("id_user1");
                Long idUser2 = resultSet.getLong("id_user2");
                Timestamp date = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsFrom = date.toLocalDateTime();
                Friendship friendship = new Friendship(idUser1, idUser2, friendsFrom);
                friendship.setID(id);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }


}
