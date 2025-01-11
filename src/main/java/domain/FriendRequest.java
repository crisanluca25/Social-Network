package domain;

import java.time.LocalDateTime;

public class FriendRequest {
    private final Long senderId;
    private final Long receiverId;
    private String status;
    private final LocalDateTime sentAt;

    public FriendRequest(Long senderId, Long receiverId, String status, LocalDateTime sentAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.sentAt = sentAt;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentAt(){
        return sentAt;
    }
}
