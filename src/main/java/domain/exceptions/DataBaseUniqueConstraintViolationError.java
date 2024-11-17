package domain.exceptions;

public class DataBaseUniqueConstraintViolationError extends MyException {
    public DataBaseUniqueConstraintViolationError(String message) {
        super(message);
    }
}
