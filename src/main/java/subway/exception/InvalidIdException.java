package subway.exception;

public class InvalidIdException extends RuntimeException {
    public static final String INVALID_LINE_ID_ERROR = "존재하지 않는 Line ID 입니다. Line ID : ";
    public static final String INVALID_SECTION_ID_ERROR = "존재하지 않는 Section ID 입니다. Section ID : ";

    public InvalidIdException(String message) {
        super(message);
    }
}
