package domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    private final User sender;
    private final User receiver;
    private final String text;
    private final LocalDateTime sentAt;

    public Message(User sender, User receiver, String text, LocalDateTime sentAt) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.sentAt = sentAt;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(sender, message.sender) && Objects.equals(receiver, message.receiver) && Objects.equals(text, message.text) && Objects.equals(sentAt, message.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, text, sentAt);
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", text='" + text + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }

}