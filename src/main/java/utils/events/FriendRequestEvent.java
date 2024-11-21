package utils.events;

import domain.Friend;
import domain.User;

public class FriendRequestEvent implements Event {

    private Long id = null;
    EventType eventType;
    private Friend deletedFriend = null;

    public FriendRequestEvent(Long id, EventType eventType) {
        this.id = id;
        this.eventType = eventType;
    }

    public FriendRequestEvent(Long id, Friend deletedFriend, EventType eventType) {
        this.id = id;
        this.deletedFriend = deletedFriend;
        this.eventType = eventType;
    }

    public FriendRequestEvent(EventType eventType) {
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
