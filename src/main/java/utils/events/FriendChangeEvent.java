package utils.events;

import domain.Friend;

public class FriendChangeEvent implements Event {

    private Long id = null;
    EventType eventType;
    private Friend deletedFriend = null;

    public FriendChangeEvent(Long id, EventType eventType) {
        this.id = id;
        this.eventType = eventType;
    }

    public FriendChangeEvent(Long id, Friend deletedFriend, EventType eventType) {
        this.id = id;
        this.deletedFriend = deletedFriend;
        this.eventType = eventType;
    }

    public FriendChangeEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getId() {
        return id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Friend getDeletedFriend() {
        return deletedFriend;
    }
}
