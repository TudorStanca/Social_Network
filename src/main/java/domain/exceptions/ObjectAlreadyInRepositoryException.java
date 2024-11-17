package domain.exceptions;

public class ObjectAlreadyInRepositoryException extends MyException {

    public <E> ObjectAlreadyInRepositoryException(E entity) {
        super("Object " + entity + " is already in Repository");
    }
}
