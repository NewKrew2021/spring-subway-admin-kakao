package subway.line.domain;

public class LineAlreadyExistException extends RuntimeException {
    private static final String LINE_ALREADY_EXIST_MESSAGE = "노선이 존재하지 않습니다.";

    public LineAlreadyExistException() {
        super(LINE_ALREADY_EXIST_MESSAGE);
    }
}
