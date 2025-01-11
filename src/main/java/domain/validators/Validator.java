package domain.validators;

@FunctionalInterface
public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
