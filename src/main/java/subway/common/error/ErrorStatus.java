package subway.common.error;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import subway.common.exception.AlreadyExistEntityException;
import subway.common.exception.NotDeletableEntityException;
import subway.common.exception.NotExistEntityException;
import subway.common.exception.NotUpdatableEntityException;

public enum ErrorStatus {
    DATA_ACCESS(HttpStatus.BAD_REQUEST),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST),
    ILLEGAL_STATE(HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_EXIST(HttpStatus.BAD_REQUEST),
    NOT_EXIST(HttpStatus.BAD_REQUEST),
    NOT_DELETABLE(HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_UPDATABLE(HttpStatus.INTERNAL_SERVER_ERROR),
    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus httpStatus;

    ErrorStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ErrorStatus of(Exception e) {
        if (e instanceof DataAccessException) {
            return DATA_ACCESS;
        }
        if (e instanceof IllegalArgumentException) {
            return ILLEGAL_ARGUMENT;
        }
        if (e instanceof IllegalStateException) {
            return ILLEGAL_STATE;
        }
        if (e instanceof AlreadyExistEntityException) {
            return ALREADY_EXIST;
        }
        if (e instanceof NotExistEntityException) {
            return NOT_EXIST;
        }
        if (e instanceof NotDeletableEntityException) {
            return NOT_DELETABLE;
        }
        if (e instanceof NotUpdatableEntityException) {
            return NOT_UPDATABLE;
        }
        return DEFAULT;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
