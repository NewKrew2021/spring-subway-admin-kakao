package subway.section.exception;

public class LeastSizeException extends IllegalArgumentException {
    public LeastSizeException() {
        super("삭제하려는 구간의 길이는 2보다 커야합니다.");
    }
}
