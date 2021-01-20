package subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exceptions.DuplicateNameException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({DuplicateNameException.class})
    public ResponseEntity BadRequestException(final RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
