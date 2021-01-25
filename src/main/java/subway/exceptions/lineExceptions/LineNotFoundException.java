package subway.exceptions.lineExceptions;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException() {
        super("해당 노선을 찾을 수 없습니다");
    }

    public LineNotFoundException(String message) {
        super(message);
    }

}
