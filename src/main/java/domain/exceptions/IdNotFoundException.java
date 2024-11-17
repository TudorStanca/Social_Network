package domain.exceptions;

public class IdNotFoundException extends MyException {

    public IdNotFoundException(Long id) {
        super("The given id " + id + " was not found");
    }
}
