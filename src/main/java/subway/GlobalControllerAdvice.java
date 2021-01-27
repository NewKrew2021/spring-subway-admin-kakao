package subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.exception.AlreadyExistsException;
import subway.section.exception.LeastSizeException;
import subway.section.exception.NoStationException;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity alreadyExistsExceptionHandler(AlreadyExistsException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NoStationException.class)
    public ResponseEntity sectionAlreadyExistExceptionHandler(NoStationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(LeastSizeException.class)
    public ResponseEntity leastSizeExceptionHandler(LeastSizeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }


}
