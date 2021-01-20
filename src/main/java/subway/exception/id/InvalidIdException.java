package subway.exception.id;

public abstract class InvalidIdException extends RuntimeException {
    public InvalidIdException(String message) {
        super(message);
    }
}
