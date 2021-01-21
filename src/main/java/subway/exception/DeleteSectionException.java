package subway.exception;

public class DeleteSectionException extends RuntimeException {

    private String message;

    public DeleteSectionException(String message) {
        super(message);
    }
}
