package subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.DuplicateNameException;
import subway.exception.EmptySectionException;
import subway.exception.NoSectionSpaceException;

import java.sql.SQLException;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity handleSQLException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity DuplicateNameException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmptySectionException.class)
    public ResponseEntity EmptySectionException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(NoSectionSpaceException.class)
    public ResponseEntity NoSectionSpaceException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
