package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {

    private Long id_from;
    private List<Long> id_to;
    private String message;
    private LocalDateTime date;
    private Long id_reply = null;

    public Message(Long id_from, List<Long> id_to, String message, LocalDateTime date) {
        this.id_from = id_from;
        this.id_to = id_to;
        this.message = message;
        this.date = date;
    }

    public Message(Long id_from, String message, LocalDateTime date) {
        this.id_from = id_from;
        this.message = message;
        this.date = date;
    }

    public Long getFrom() {
        return id_from;
    }

    public List<Long> getTo() {
        return id_to;
    }

    public void setTo(List<Long> id_to) {
        this.id_to = id_to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getReply() {
        return id_reply;
    }

    public void setReply(Long id_reply) {
        this.id_reply = id_reply;
    }
}
