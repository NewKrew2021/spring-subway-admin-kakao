package subway.exceptions.lineExceptions;

public class LineNothingToUpdateException extends RuntimeException {
    @Override
    public String getMessage() {
        return "업데이트가 가능한 노선이 존재하지 않습니다.";
    }
}
