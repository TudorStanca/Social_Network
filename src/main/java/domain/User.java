package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Optional;

public class User extends Entity<Long> {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    /**
     * Default constructor for JSON
     */
    private User() {
    } // I NEED THIS FOR JSON TO WORK

    /**
     * Constructor for User
     *
     * @param firstName The first name of the user
     * @param lastName  The last name of the user
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Optional.ofNullable(getId()).ifPresent(x -> sb.append(x).append(". "));
        sb.append(firstName).append(" ").append(lastName);
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
        User other = (User) obj;
        return getId().equals(other.getId()) && firstName.equals(other.firstName) && lastName.equals(other.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), firstName, lastName);
    }
}
