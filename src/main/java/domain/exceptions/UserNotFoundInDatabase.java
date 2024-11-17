package domain.exceptions;

public class UserNotFoundInDatabase extends MyException {

    public UserNotFoundInDatabase(String email) {
        super("User with email " + email + " and password does not exist");
    }
}
