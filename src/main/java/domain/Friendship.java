package domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Long> {
    private final Long idUser1;
    private final Long idUser2;
    private final LocalDateTime friendsFrom;

    public Friendship(Long idUser1, Long idUser2, LocalDateTime friendsFrom) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.friendsFrom = friendsFrom;
    }


    public Long getIdUser1() {
        return idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }
}
