package subway.exception.id;

public class InvalidLineIdException extends InvalidIdException {
    private static final String INVALID_LINE_ID_ERROR = "존재하지 않는 Line ID 입니다. Line ID : ";

    public InvalidLineIdException(Long id) {
        super(INVALID_LINE_ID_ERROR + id);
    }
}
