package subway.station.exceptions;

public class InvalidDeleteException extends RuntimeException {
    public InvalidDeleteException(String message) {
        super(message);
    }
}
