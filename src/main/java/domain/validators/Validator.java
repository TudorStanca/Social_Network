package domain.validators;

import domain.exceptions.ValidationException;

public interface Validator<T> {

    /**
     * Validates the entity
     *
     * @param entity The entity that is being validated
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}
