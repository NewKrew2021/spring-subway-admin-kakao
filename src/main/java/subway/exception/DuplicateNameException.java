package subway.exception;

public class DuplicateNameException extends RuntimeException {

    public DuplicateNameException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "이름 중복 : " + super.getMessage();
    }
}
