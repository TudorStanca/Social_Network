package domain.dto;

import domain.User;

import java.time.LocalDateTime;

public class UserDTO {

    private Long id = null;
    private String firstName = null;
    private String lastName = null;
    private LocalDateTime date = null;

    public UserDTO(Long id, String firstName, String lastName, LocalDateTime date) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public UserDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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
