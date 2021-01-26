package subway.line.exception;

public class LineAlreadyExistException extends IllegalArgumentException{
    public LineAlreadyExistException() {
        super("같은 이름으로 존재하는 노선이 존재합니다.");
    }
}
