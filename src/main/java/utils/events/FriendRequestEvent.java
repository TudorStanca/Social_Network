package utils.events;

import domain.User;

public class FriendRequestEvent implements Event {

    private Long id;

    public FriendRequestEvent(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
