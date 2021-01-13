package subway.exceptions;

public class DuplicateLineNameException extends RuntimeException {

    public DuplicateLineNameException(String message) {
        super(message);
    }
}
