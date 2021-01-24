package subway.exceptions.lineExceptions;

public class LineDuplicatedException extends RuntimeException {
    public LineDuplicatedException() {
        super("중복된 노선입니다");
    }

    public LineDuplicatedException(String message) {
        super(message);
    }

}
