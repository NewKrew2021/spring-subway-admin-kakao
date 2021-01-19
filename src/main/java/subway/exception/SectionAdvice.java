package subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.exceptions.EmptySectionException;
import subway.exception.exceptions.InvalidSectionException;

@ControllerAdvice
public class SectionAdvice {

    @ExceptionHandler({InvalidSectionException.class, EmptySectionException.class})
    public ResponseEntity<String> internalServerErrorHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
