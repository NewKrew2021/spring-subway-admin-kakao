package subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.exceptions.DuplicateStationNameException;

@ControllerAdvice
public class StationAdvice {

    @ExceptionHandler(DuplicateStationNameException.class)
    public ResponseEntity<String> badRequestErrorHandler(DuplicateStationNameException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
