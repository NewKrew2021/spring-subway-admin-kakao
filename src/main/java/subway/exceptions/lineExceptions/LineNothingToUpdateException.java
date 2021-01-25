package subway.exceptions.lineExceptions;

public class LineNothingToUpdateException extends RuntimeException {
    public LineNothingToUpdateException() {
        super("업데이트가 가능한 노선이 존재하지 않습니다.");
    }

    public LineNothingToUpdateException(String message) {
        super(message);
    }

}
