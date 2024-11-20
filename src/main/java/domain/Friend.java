package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Friend extends Entity<Long> {

    @JsonProperty("friendship")
    private Tuple<Long> friendship;

    private boolean status;

    private LocalDateTime friendsFrom;

    /**
     * Default constructor for JSON
     */
    private Friend() {
    } // I NEED THIS FOR JSON TO WORK

    /**
     * Constructor for Friend
     *
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     */
    public Friend(Long id1, Long id2, boolean status, LocalDateTime friendsFrom) {
        friendship = new Tuple<>(id1, id2);
        this.status = status;
        this.friendsFrom = friendsFrom;
    }

    public Tuple<Long> getFriendship() {
        return friendship;
    }

    public Long getFirstFriend() {
        return friendship.getFirst();
    }

    public Long getSecondFriend() {
        return friendship.getSecond();
    }

    public boolean getStatus() {
        return status;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public boolean hasFriend(Long id) {
        return friendship.hasEntity(id);
    }

    public void setFriendship(Tuple<Long> friendship) {
        this.friendship = friendship;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Optional.ofNullable(getId()).ifPresent(x -> sb.append(x).append(". "));
        sb.append("(").append(getFirstFriend()).append(", ").append(getSecondFriend()).append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Friend other = (Friend) obj;
        return getId().equals(other.getId()) && friendship.equals(other.friendship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), friendship);
    }
}
