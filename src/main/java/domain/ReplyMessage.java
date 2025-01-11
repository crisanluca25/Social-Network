package domain;

import java.time.LocalDateTime;

public class ReplyMessage extends Message {
    private final Message replyTo;

    public ReplyMessage(User sender, User receiver, String text, LocalDateTime sentAt, Message replyTo) {
        super(sender, receiver, text, sentAt);
        this.replyTo = replyTo;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    @Override
    public String toString() {
        return "Reply to: " + replyTo.getSender().getUsername() + " - " + getText();
    }
}
