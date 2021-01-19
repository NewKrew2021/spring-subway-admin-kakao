package subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.exceptions.DuplicateLineNameException;
import subway.exception.exceptions.InvalidLineArgumentException;

@ControllerAdvice
public class LineAdvice {

    @ExceptionHandler({InvalidLineArgumentException.class, DuplicateLineNameException.class})
    public ResponseEntity<String> badRequestErrorHandler(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
