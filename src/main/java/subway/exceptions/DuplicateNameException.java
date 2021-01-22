package subway.exceptions;

public class DuplicateNameException extends RuntimeException{
    public DuplicateNameException(String msg) {
        super(msg);
    }
}
