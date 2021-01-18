package subway.exception;

public class SectionDistanceExceedException extends RuntimeException{

    private String message;

    public SectionDistanceExceedException(String message) {
        super(message);
    }
}
