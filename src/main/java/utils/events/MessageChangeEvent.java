package utils.events;

import java.util.List;

public class MessageChangeEvent implements Event {

    private EventType type;
    private Long idFrom;
    private List<Long> idTo;

    public MessageChangeEvent(EventType type, Long idFrom, List<Long> idTo) {
        this.type = type;
        this.idFrom = idFrom;
        this.idTo = idTo;
    }

    public EventType getType() {
        return type;
    }

    public Long getIdFrom() {
        return idFrom;
    }

    public List<Long> getIdTo() {
        return idTo;
    }

}
