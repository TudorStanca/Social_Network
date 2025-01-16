package utils.events;

public class UserChangeEvent implements Event {

    private Long id;
    private EventType eventType;
    private String imagePath;

    public UserChangeEvent(EventType eventType, Long id, String imagePath) {
        this.eventType = eventType;
        this.imagePath = imagePath;
        this.id = id;
    }

    public UserChangeEvent(EventType eventType, Long id) {
        this.eventType = eventType;
        this.id = id;
    }

    public UserChangeEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Long getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }
}
