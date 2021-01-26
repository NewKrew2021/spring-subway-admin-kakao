package subway.exception.exceptions;

public class InvalidValueException extends RuntimeException {

    public InvalidValueException() {
        super();
    }

    public InvalidValueException(InvalidValueExceptionEnum invalidValueExceptionEnum) {
        super(invalidValueExceptionEnum.getErrMessage());
    }
}
