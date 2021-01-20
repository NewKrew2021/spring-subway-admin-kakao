package subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.common.error.ErrorResponse;
import subway.common.error.ErrorStatus;
import subway.common.exception.AlreadyExistEntityException;
import subway.common.exception.NotDeletableEntityException;
import subway.common.exception.NotExistEntityException;
import subway.common.exception.NotUpdatableEntityException;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        ErrorResponse response = ErrorResponse.of(ErrorStatus.SQL, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse response = ErrorResponse.of(ErrorStatus.ILLEGAL_ARGUMENT, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        ErrorResponse response = ErrorResponse.of(ErrorStatus.ILLEGAL_STATE, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(AlreadyExistEntityException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistEntityException(AlreadyExistEntityException e) {
        ErrorResponse response = ErrorResponse.of(ErrorStatus.ALREADY_EXIST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(NotExistEntityException.class)
    public ResponseEntity<ErrorResponse> handleNotExistEntityException(NotExistEntityException e) {
        ErrorResponse response = ErrorResponse.of(ErrorStatus.NOT_EXIST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(NotDeletableEntityException.class)
    public ResponseEntity<ErrorResponse> handleNotDeletableEntityException(NotDeletableEntityException e) {
        ErrorResponse response = ErrorResponse.of(ErrorStatus.NOT_DELETABLE, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(NotUpdatableEntityException.class)
    public ResponseEntity<ErrorResponse> handleNotUpdatableEntityException(NotUpdatableEntityException e) {
        ErrorResponse response = ErrorResponse.of(ErrorStatus.NOT_UPDATABLE, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
}
