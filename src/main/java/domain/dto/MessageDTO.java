package domain.dto;

import java.time.LocalDateTime;

public class MessageDTO {

    private Long idMessage;
    private Long idFrom;
    private Long idTo;
    private String text;
    private LocalDateTime date;

    private Long idMessageReply = null;
    private String textReply = null;
    private LocalDateTime dateReply = null;

    public MessageDTO(Long idMessage, Long idFrom, Long idTo, String text, LocalDateTime date, Long idMessageReply, String textReply, LocalDateTime dateReply) {
        this.idMessage = idMessage;
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.text = text;
        this.date = date;
        this.idMessageReply = idMessageReply;
        this.textReply = textReply;
        this.dateReply = dateReply;
    }

    public MessageDTO(Long idMessage, Long idFrom, Long idTo, String text, LocalDateTime date){
        this.idMessage = idMessage;
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.text = text;
        this.date = date;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public Long getIdFrom() {
        return idFrom;
    }

    public Long getIdTo() {
        return idTo;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Long getIdMessageReply() {
        return idMessageReply;
    }

    public String getTextReply() {
        return textReply;
    }

    public LocalDateTime getDateReply() {
        return dateReply;
    }
}
