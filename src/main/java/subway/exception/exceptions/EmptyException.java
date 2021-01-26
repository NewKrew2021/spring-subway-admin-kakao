package subway.exception.exceptions;

public class EmptyException extends RuntimeException {

    public EmptyException(EmptyExceptionEnum emptyExceptionEnum) {
        super(emptyExceptionEnum.getErrMessage());
    }
}
