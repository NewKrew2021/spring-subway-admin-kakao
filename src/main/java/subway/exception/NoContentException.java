package subway.exception;

public class NoContentException extends RuntimeException {

    public NoContentException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "데이터 없음 : " + super.getMessage();
    }
}
