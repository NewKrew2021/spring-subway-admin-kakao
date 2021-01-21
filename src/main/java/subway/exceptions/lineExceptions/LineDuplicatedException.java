package subway.exceptions.lineExceptions;

public class LineDuplicatedException extends RuntimeException {
    @Override
    public String getMessage() {
        return "중복된 노선입니다";
    }
}
