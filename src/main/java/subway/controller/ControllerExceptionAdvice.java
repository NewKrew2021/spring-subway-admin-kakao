package subway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionAdvice.class);

    @ExceptionHandler(SQLException.class)
    public ResponseEntity handleSQLException() {
        log.error("sqlException 발생");
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity DuplicateNameException() {
        log.error("DuplicateNameException 발생");
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmptySectionException.class)
    public ResponseEntity EmptySectionException() {
        log.error("EmptySectionException 발생");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(NoSectionSpaceException.class)
    public ResponseEntity NoSectionSpaceException() {
        log.error("NoSectionSpaceException 발생");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
