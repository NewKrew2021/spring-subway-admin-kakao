package subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.line.domain.LineAlreadyExistException;
import subway.line.domain.LineNotFoundException;
import subway.line.domain.SectionInsertException;
import subway.line.domain.SectionNotValidDeleteException;

@RestControllerAdvice
public class CustomExceptonHandler {

    @ExceptionHandler({LineNotFoundException.class})
    public ResponseEntity lineNotFound() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(LineAlreadyExistException.class)
    public ResponseEntity lineAlreadyExist() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({SectionInsertException.class})
    public ResponseEntity sectionInsertException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler({SectionNotValidDeleteException.class})
    public ResponseEntity sectionNotValidDelete() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
