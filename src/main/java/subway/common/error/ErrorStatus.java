package subway.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorStatus {
    // Common
    SQL(HttpStatus.BAD_REQUEST),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST),
    ILLEGAL_STATE(HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_EXIST(HttpStatus.BAD_REQUEST),
    NOT_EXIST(HttpStatus.BAD_REQUEST),
    NOT_DELETABLE(HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_UPDATABLE(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus httpStatus;

    ErrorStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
