package subway.line.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.line.domain.LineAlreadyExistException;
import subway.line.domain.LineNotFoundException;
import subway.line.domain.SectionInsertException;
import subway.line.domain.SectionNotValidDeleteException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({LineNotFoundException.class, LineAlreadyExistException.class})
    public ResponseEntity lineNotFound() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({SectionInsertException.class, SectionNotValidDeleteException.class})
    public ResponseEntity illegal() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
