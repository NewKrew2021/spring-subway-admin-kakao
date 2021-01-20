package subway.exceptions;

public class InvalidStationArgumentException extends RuntimeException{
    public InvalidStationArgumentException(String message) {
        super(message);
    }
}
