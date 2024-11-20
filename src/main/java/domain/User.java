package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Optional;

public class User extends Entity<Long> {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    private String email;
    private String password;

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
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        //Optional.ofNullable(getId()).ifPresent(x -> sb.append(x).append(". "));
        return firstName + " " + lastName;
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
        return email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
