package subway.exception.exceptions;

public class DuplicateException extends RuntimeException {

    public DuplicateException(DuplicateExceptionEnum duplicateExceptionEnum) {
        super(duplicateExceptionEnum.getErrMessage());
    }
}
