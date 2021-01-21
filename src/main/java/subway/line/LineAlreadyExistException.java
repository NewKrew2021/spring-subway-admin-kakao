package subway.line;

public class LineAlreadyExistException extends RuntimeException {
    public LineAlreadyExistException() {
        super("노선이 존재하지 않습니다.");
    }
}
