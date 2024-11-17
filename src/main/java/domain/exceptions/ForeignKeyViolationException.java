package domain.exceptions;

public class ForeignKeyViolationException extends MyException {

    public<E> ForeignKeyViolationException(E entity) {
        super("The given entity: " + entity.toString() + " does not exist!");
    }
}
