package subway.exception.exceptions;

public class DuplicateStationNameException extends RuntimeException {

    public DuplicateStationNameException(String message) {
        super(message);
    }
}