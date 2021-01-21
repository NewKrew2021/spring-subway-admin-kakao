package subway.line.exceptions;

public class NoSuchLineException extends RuntimeException {
    private static final String MESSAGE_FORMAT = "id:%d line을 찾을 수 없습니다.";

    public NoSuchLineException(Long lineId) {
        super(String.format(MESSAGE_FORMAT, lineId));
    }
}
