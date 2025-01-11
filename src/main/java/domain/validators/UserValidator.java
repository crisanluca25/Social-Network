package domain.validators;

import domain.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        String errorMessage = "";

        if (entity.getUsername().isEmpty()) {
            errorMessage += "Username can't be null! ";
        }
        if (entity.getEmail().isEmpty()) {
            errorMessage += "Email can't be null! ";
        }
        if (entity.getPassword().isEmpty()) {
            errorMessage += "Password can't be null! ";
        }
        System.out.println(errorMessage);
        if (!errorMessage.isEmpty()) {
            throw new ValidationException(errorMessage);
        }
    }
}
