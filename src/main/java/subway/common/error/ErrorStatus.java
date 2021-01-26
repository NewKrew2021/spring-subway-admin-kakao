package subway.common.error;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import subway.common.exception.AlreadyExistEntityException;
import subway.common.exception.NotDeletableEntityException;
import subway.common.exception.NotExistEntityException;
import subway.common.exception.NotUpdatableEntityException;

import java.util.Arrays;

public enum ErrorStatus {
    DATA_ACCESS(HttpStatus.BAD_REQUEST, DataAccessException.class),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, IllegalArgumentException.class),
    ILLEGAL_STATE(HttpStatus.INTERNAL_SERVER_ERROR, IllegalStateException.class),
    ALREADY_EXIST(HttpStatus.BAD_REQUEST, AlreadyExistEntityException.class),
    NOT_EXIST(HttpStatus.BAD_REQUEST, NotExistEntityException.class),
    NOT_DELETABLE(HttpStatus.INTERNAL_SERVER_ERROR, NotDeletableEntityException.class),
    NOT_UPDATABLE(HttpStatus.INTERNAL_SERVER_ERROR, NotUpdatableEntityException.class),
    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR, Exception.class);

    private final HttpStatus httpStatus;
    private final Class<? extends Exception> exception;

    ErrorStatus(HttpStatus httpStatus, Class<? extends Exception> exception) {
        this.httpStatus = httpStatus;
        this.exception = exception;
    }

    public static ErrorStatus of(Exception exception) {
        return Arrays.stream(ErrorStatus.values())
                .filter(errorStatus -> errorStatus.exception.isInstance(exception))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 예외 타입이 없습니다."));
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
