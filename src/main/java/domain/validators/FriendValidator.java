package domain.validators;

import domain.Friend;
import domain.exceptions.ValidationException;

public class FriendValidator implements Validator<Friend> {

    @Override
    public void validate(Friend entity) throws ValidationException {
        String errors = "";

        if (entity.getFirstFriend().equals(entity.getSecondFriend())) {
            errors += "A user can't be friends with themselves\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}