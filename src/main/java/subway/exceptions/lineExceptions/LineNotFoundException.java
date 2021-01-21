package subway.exceptions.lineExceptions;

public class LineNotFoundException extends RuntimeException {
    @Override
    public String getMessage() {
        return "해당 노선을 찾을 수 없습니다";
    }
}
