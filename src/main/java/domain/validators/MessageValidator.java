package domain.validators;

import domain.Message;
import domain.exceptions.ValidationException;

public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message entity) throws ValidationException {
        String errors = "";

        if (entity.getMessage().isEmpty()) {
            errors += "Can't send an empty message";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
