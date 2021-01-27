package subway.exception.exceptions;

public class FailedDeleteException extends RuntimeException {

    public FailedDeleteException(FailedDeleteExceptionEnum failedDeleteExceptionEnum) {
        super(failedDeleteExceptionEnum.getErrMessage());
    }
}
