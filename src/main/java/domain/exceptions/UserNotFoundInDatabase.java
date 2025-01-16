package domain.exceptions;

public class UserNotFoundInDatabase extends MyException {

    public UserNotFoundInDatabase(String email) {
        super("User with email " + email + " does not exist");
    }
}
