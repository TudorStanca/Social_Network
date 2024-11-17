package domain.validators;

import domain.User;
import domain.exceptions.ValidationException;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";

        if (entity.getFirstName().isEmpty()) {
            errors += "First name is required!\n";
        } else if (Character.isLowerCase(entity.getFirstName().charAt(0))) {
            errors += "First name starts with lower case letter!\n";
        }

        if (entity.getLastName().isEmpty()) {
            errors += "Last name is required!\n";
        } else if (Character.isLowerCase(entity.getLastName().charAt(0))) {
            errors += "Last name starts with lower case letter!\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
