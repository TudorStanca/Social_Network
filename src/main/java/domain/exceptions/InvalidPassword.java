package domain.exceptions;

public class InvalidPassword extends MyException {
    public InvalidPassword() {
        super("The password is invalid. Please try again.");
    }
}
