package subway.common.exception;

public class NotDeletableEntityException extends RuntimeException {
    public NotDeletableEntityException(String message) {
        super(message);
    }
}
