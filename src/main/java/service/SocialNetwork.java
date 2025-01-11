package service;

import domain.FriendRequest;
import domain.Friendship;
import domain.Message;
import domain.User;
import domain.validators.ValidationException;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import repository.FriendRequestDBRepository;
import repository.FriendshipDBRepository;
import repository.MessageDBRepository;
import repository.UserDBRepository;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class SocialNetwork {

    private final UserDBRepository userRepository;
    private final FriendshipDBRepository friendshipRepository;
    private final FriendRequestDBRepository friendRequestRepository;
    private final MessageDBRepository messageRepository;



    public SocialNetwork(UserDBRepository userRepository, FriendshipDBRepository friendshipRepository, FriendRequestDBRepository friendRequestRepository, MessageDBRepository messageRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.messageRepository = messageRepository;
    }



    public Iterable<User> getUsers() {
        return userRepository.getAll();
    }

    public User findUser(Long id) {
        return userRepository.find(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
    }

    public Long getNewUserId() {
        Long[] id = {0L};
        userRepository.getAll().forEach(u -> id[0] = u.getId());
        return id[0] + 1;
    }

    public void addUser(User user) {
        user.setID(getNewUserId());
        userRepository.save(user);
    }

    public Iterable<Friendship> getFriendships() {
        return friendshipRepository.getAll();
    }

    public User removeUser(Long id) {
        try {
            User u = userRepository.find(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
            Vector<Long> toDelete = new Vector<>();
            getFriendships().forEach(friendship -> {
                if (friendship.getIdUser2().equals(id) || friendship.getIdUser1().equals(id)) {
                    toDelete.add(friendship.getId());
                }
            });
            toDelete.forEach(friendshipRepository::delete);
            User user = userRepository.delete(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
            u.getFriends().forEach(friend -> friend.removeFriend(u));
            return user;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user! ");
        } catch (ValidationException v) {
            System.out.println();
        }
        return null;
    }


    public Long getNewFriendshipId() {
        Long[] id = {0L};
        friendshipRepository.getAll().forEach(f -> id[0] = f.getId());
        return id[0] + 1;
    }

    public void addFriendship(Friendship friendship) {

        User user1 = userRepository.find(friendship.getIdUser1())
                .orElseThrow(() -> new ValidationException("User with ID " + friendship.getIdUser1() + " does not exist!"));
        User user2 = userRepository.find(friendship.getIdUser2())
                .orElseThrow(() -> new ValidationException("User with ID " + friendship.getIdUser2() + " does not exist!"));


        if (friendship.getIdUser1().equals(friendship.getIdUser2())) {
            throw new ValidationException("Users cannot be friends with themselves!");
        }

        boolean friendshipExists = StreamSupport.stream(getFriendships().spliterator(), false)
                .anyMatch(f -> (f.getIdUser1().equals(friendship.getIdUser1()) && f.getIdUser2().equals(friendship.getIdUser2()))
                        || (f.getIdUser1().equals(friendship.getIdUser2()) && f.getIdUser2().equals(friendship.getIdUser1())));

        if (friendshipExists) {
            throw new ValidationException("The friendship already exists!");
        }

        friendship.setID(getNewFriendshipId());
        friendshipRepository.save(friendship);

        if (!user1.getFriends().contains(user2)) {
            user1.addFriend(user2);
        }
        if (!user2.getFriends().contains(user1)) {
            user2.addFriend(user1);
        }
    }


    public void removeFriendship(Long id1, Long id2) {
        User user1 = null;
        User user2 = null;
        try {
            user1 = userRepository.find(id1).orElseThrow(() -> new ValidationException("User with id " + id1 + " doesn't exist!"));
            user2 = userRepository.find(id2).orElseThrow(() -> new ValidationException("User with id " + id2 + " doesn't exist!"));
        } catch (ValidationException v) {
            System.out.println();
        }
        Long[] id = {0L};
        friendshipRepository.getAll().forEach(f ->{
            if((f.getIdUser1().equals(id1) && f.getIdUser2().equals(id2) ||
                    (f.getIdUser1().equals(id2) && f.getIdUser2().equals(id1)))){
                id[0] = f.getId();
            }
        });
        if (id[0] == 0L)
            throw new IllegalArgumentException("The friendship doesn't exist!");
        friendshipRepository.delete(id[0]);

        assert user1 != null;
        user1.removeFriend(user2);
        assert user2 != null;
        user2.removeFriend(user1);
    }

    public List<User> getFriends(User user) {
        List<Friendship> friendships = friendshipRepository.findByUser(user.getId());

        return friendships.stream()
                .map(friendship -> {
                    Long friendId = friendship.getIdUser1().equals(user.getId())
                            ? friendship.getIdUser2()
                            : friendship.getIdUser1();
                    return userRepository.find(friendId).orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }


    public void sendFriendRequest(FriendRequest request) {
        boolean requestExists = StreamSupport.stream(getPendingRequests(request.getReceiverId()).spliterator(), false)
                .anyMatch(r -> (r.getSenderId().equals(request.getSenderId()) && r.getReceiverId().equals(request.getReceiverId())) ||
                        (r.getSenderId().equals(request.getReceiverId()) && r.getReceiverId().equals(request.getSenderId())));

        if (requestExists) {
            throw new ValidationException("Friend request already exists!");
        }

        friendRequestRepository.save(request);
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }


    public List<FriendRequest> getPendingRequests(Long receiverId) {
        return friendRequestRepository.findByReceiver(receiverId).stream()
                .filter(request -> request.getStatus().equals("Pending"))
                .toList();
    }

    public void acceptFriendRequest(FriendRequest request) {
        friendRequestRepository.delete(request.getSenderId(), request.getReceiverId());
        Friendship friendship = new Friendship(request.getSenderId(), request.getReceiverId(), LocalDateTime.now());
        addFriendship(friendship);
    }

    public void rejectFriendRequest(FriendRequest request) {
        friendRequestRepository.delete(request.getSenderId(), request.getReceiverId());
    }

    public String getUsernameById(Long userId) {
        User user = StreamSupport.stream(getUsers().spliterator(), false)
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
        return user != null ? user.getUsername() : "Unknown";
    }

    public List<Message> getMessages(User user1, User user2) {
        return messageRepository.getMessages(user1, user2);
    }

    public List<User> getFriendsPaginated(User user, int offset, int limit) {
        List<Friendship> friendships = friendshipRepository.findByUserPaginated(user.getId(), offset, limit);
        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
            Long friendId = friendship.getIdUser1().equals(user.getId())
                    ? friendship.getIdUser2()
                    : friendship.getIdUser1();
            userRepository.find(friendId).ifPresent(friends::add);
        }
        return friends;
    }

    public int getTotalFriends(User user) {
        List<Friendship> friendships = friendshipRepository.findByUser(user.getId());
        return friendships.size();
    }

    public void openFileChooser(ImageView profilePicture, Long userId) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );
        String userHome = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userHome));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                profilePicture.setImage(image);
                userRepository.updateProfilePicture(userId, selectedFile);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Could not update profile picture: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public byte[] getDefaultProfilePicture() {
        try {
            return getClass().getResourceAsStream("/images/defaultprofile.png")
                    .readAllBytes();
        } catch (Exception e) {
            return new byte[0];
        }
    }


}