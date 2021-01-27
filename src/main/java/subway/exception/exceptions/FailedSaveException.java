package subway.exception.exceptions;

public class FailedSaveException extends RuntimeException {

    public FailedSaveException(FailedSaveExceptionEnum failedSaveExceptionEnum) {
        super(failedSaveExceptionEnum.getErrMessage());
    }
}
