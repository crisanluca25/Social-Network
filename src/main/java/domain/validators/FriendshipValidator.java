package domain.validators;

import domain.Friendship;
import domain.User;
import repository.UserDBRepository;

import java.util.Optional;

public class FriendshipValidator implements Validator<Friendship> {

    private UserDBRepository repo;

    public FriendshipValidator(UserDBRepository repo) {
        this.repo = repo;
    }

    @Override
    public void validate(Friendship entity) throws ValidationException {

        Optional<User> u1 = repo.find(entity.getIdUser1());
        Optional<User> u2 = repo.find(entity.getIdUser2());

        if (entity.getIdUser1() == null || entity.getIdUser2() == null)
            throw new ValidationException("The id can't be null! ");
        if (u1.isEmpty() || u2.isEmpty())
            throw new ValidationException("The id doesn't exist! ");
    }
}
