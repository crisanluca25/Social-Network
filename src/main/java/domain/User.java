package domain;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class User extends Entity<Long>{
    private String email;
    private String username;
    private String password;
    private String salt;
    private byte[] profilePicture;
    List<User> friends;

    public User(String email, String username, String password, String salt, byte[] profilePicture) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.profilePicture = profilePicture;
        friends = new Vector<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() { return salt; }

    public void setSalt(String salt) { this.salt = salt; }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void addFriend(User user) {
        friends.add(user);
    }

    public void removeFriend(User user) {
        friends.remove(user);
    }

    public List<User> getFriends(){
        return friends;
    }

    public User getFriendById(Long id) {
        return friends.stream()
                .filter(friend -> friend.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password, email, friends);
    }

    @Override
    public String toString() {
        return username;
    }
}
