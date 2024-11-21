package domain.dto;

import java.time.LocalDateTime;

public class FriendDTO {

    private Long idFriendship = null;
    private Long idFriend;
    private String firstName;
    private String lastName;
    private LocalDateTime date = null;

    public FriendDTO(Long idFriendship, Long idFriend, String firstName, String lastName, LocalDateTime date) {
        this.idFriendship = idFriendship;
        this.idFriend = idFriend;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public FriendDTO(Long idFriend, String firstName, String lastName) {
        this.idFriend = idFriend;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getIdFriendship() {
        return idFriendship;
    }

    public Long getIdFriend() {
        return idFriend;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getDate() {
        return date;
    }
}