package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.exceptions.*;

@ControllerAdvice
public class LineAdvice {

    @ExceptionHandler({InvalidLineArgumentException.class, DuplicateLineNameException.class, FailedDeleteLineException.class})
    public ResponseEntity<String> badRequestErrorHandler(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({EmptyLineException.class, InvalidLineException.class})
    public ResponseEntity<String> internalServerErrorHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
